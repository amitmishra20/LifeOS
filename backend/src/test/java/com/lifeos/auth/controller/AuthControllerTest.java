package com.lifeos.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.auth.dto.LoginRequest;
import com.lifeos.auth.dto.RegisterRequest;
import com.lifeos.security.CookieService;
import com.lifeos.security.TokenProvider;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("1. Successful registration sets HttpOnly cookie and returns safe user data")
        void shouldRegisterSuccessfully() throws Exception {
            RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "Password123!");

            MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("Jane Doe"))
                    .andExpect(jsonPath("$.email").value("jane@example.com"))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.passwordHash").doesNotExist())
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("lifeos_token=")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                    .andReturn();

            // Verify user was persisted and password was securely hashed
            User persisted = userRepository.findByEmail("jane@example.com").orElseThrow();
            assertThat(persisted.getName()).isEqualTo("Jane Doe");
            assertThat(persisted.getPasswordHash()).isNotEqualTo("Password123!");
            assertThat(passwordEncoder.matches("Password123!", persisted.getPasswordHash())).isTrue();
        }

        @Test
        @DisplayName("2. Duplicate email registration returns 409 Conflict")
        void shouldRejectDuplicateEmail() throws Exception {
            User existing = new User("Jane Existing", "jane@example.com", passwordEncoder.encode("ExistingPass123!"));
            userRepository.save(existing);

            RegisterRequest request = new RegisterRequest("Jane New", "JANE@EXAMPLE.COM", "NewPassword123!");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"))
                    .andExpect(jsonPath("$.message").value("An account with this email already exists"));
        }

        @Test
        @DisplayName("3. Invalid registration returns 400 Bad Request with field errors")
        void shouldRejectInvalidRegistration() throws Exception {
            RegisterRequest request = new RegisterRequest("", "not-an-email", "short");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.fieldErrors.name").exists())
                    .andExpect(jsonPath("$.fieldErrors.email").exists())
                    .andExpect(jsonPath("$.fieldErrors.password").exists());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("4. Successful login returns 200 OK and sets auth cookie")
        void shouldLoginSuccessfully() throws Exception {
            User user = new User("John Doe", "john@example.com", passwordEncoder.encode("SecretPass123!"));
            userRepository.save(user);

            LoginRequest request = new LoginRequest("john@example.com", "SecretPass123!");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId()))
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.passwordHash").doesNotExist())
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("lifeos_token=")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")));
        }

        @Test
        @DisplayName("5. Invalid login returns 401 without leaking existence")
        void shouldRejectInvalidCredentials() throws Exception {
            User user = new User("John Doe", "john@example.com", passwordEncoder.encode("SecretPass123!"));
            userRepository.save(user);

            // Wrong password
            LoginRequest wrongPass = new LoginRequest("john@example.com", "WrongPassword123!");
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongPass)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));

            // Nonexistent email
            LoginRequest nonExistent = new LoginRequest("ghost@example.com", "SecretPass123!");
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistent)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }
    }

    @Nested
    @DisplayName("Current User & Session Tests")
    class CurrentUserTests {

        @Test
        @DisplayName("6. Current authenticated user returns 200 OK with safe user details")
        void shouldReturnCurrentAuthenticatedUser() throws Exception {
            User user = new User("Alex River", "alex@example.com", passwordEncoder.encode("ValidPass123!"));
            user = userRepository.save(user);

            String token = tokenProvider.generateToken(user.getId(), user.getEmail());

            mockMvc.perform(get("/api/v1/auth/me")
                            .cookie(new Cookie("lifeos_token", token))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId()))
                    .andExpect(jsonPath("$.name").value("Alex River"))
                    .andExpect(jsonPath("$.email").value("alex@example.com"))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }

        @Test
        @DisplayName("7. Unauthenticated current-user request returns 401 Unauthorized")
        void shouldRejectUnauthenticatedCurrentUser() throws Exception {
            mockMvc.perform(get("/api/v1/auth/me")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.path").value("/api/v1/auth/me"));
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("8. Logout invalidates cookie by setting Max-Age=0")
        void shouldLogoutSuccessfully() throws Exception {
            mockMvc.perform(post("/api/v1/auth/logout"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Logged out successfully"))
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("lifeos_token=")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));
        }
    }

    @Nested
    @DisplayName("Security & Protection Tests")
    class SecurityProtectionTests {

        @Test
        @DisplayName("9. Protected endpoints reject unauthenticated requests with 401")
        void shouldRejectUnauthenticatedProtectedEndpoints() throws Exception {
            mockMvc.perform(get("/api/v1/goals")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        }

        @Test
        @DisplayName("10. Password hash is never exposed in any API response")
        void shouldNeverExposePasswordHash() throws Exception {
            RegisterRequest registerRequest = new RegisterRequest("Secret User", "secret@example.com", "TopSecret123!");

            MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String regContent = regResult.getResponse().getContentAsString();
            assertThat(regContent).doesNotContain("passwordHash");
            assertThat(regContent).doesNotContain("TopSecret123!");

            LoginRequest loginRequest = new LoginRequest("secret@example.com", "TopSecret123!");
            MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String loginContent = loginResult.getResponse().getContentAsString();
            assertThat(loginContent).doesNotContain("passwordHash");
            assertThat(loginContent).doesNotContain("TopSecret123!");
        }
    }
}
