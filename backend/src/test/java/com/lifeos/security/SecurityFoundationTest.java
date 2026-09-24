package com.lifeos.security;

import com.lifeos.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SecurityFoundationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("BCrypt Password Encoder Tests")
    class PasswordEncoderTests {

        @Test
        @DisplayName("Should encode password and generate different salts on subsequent hashes")
        void shouldEncodeWithSalts() {
            String rawPassword = "SuperSecretPassword123!";
            String hash1 = passwordEncoder.encode(rawPassword);
            String hash2 = passwordEncoder.encode(rawPassword);

            assertThat(hash1).isNotEmpty().isNotEqualTo(rawPassword);
            assertThat(hash2).isNotEmpty().isNotEqualTo(rawPassword);
            assertThat(hash1).isNotEqualTo(hash2); // BCrypt random salts
        }

        @Test
        @DisplayName("Should match correct password and reject incorrect password")
        void shouldMatchCorrectPassword() {
            String rawPassword = "CorrectHorseBatteryStaple";
            String hash = passwordEncoder.encode(rawPassword);

            assertThat(passwordEncoder.matches(rawPassword, hash)).isTrue();
            assertThat(passwordEncoder.matches("WrongPassword", hash)).isFalse();
        }
    }

    @Nested
    @DisplayName("HMAC-SHA256 Token Provider Tests")
    class TokenProviderTests {

        @Test
        @DisplayName("Should generate token, validate, and extract correct claims")
        void shouldGenerateAndExtractClaims() {
            Long userId = 42L;
            String email = "jane.dev@example.com";

            String token = tokenProvider.generateToken(userId, email);

            assertThat(token).isNotBlank();
            assertThat(tokenProvider.validateToken(token)).isTrue();
            assertThat(tokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
            assertThat(tokenProvider.getEmailFromToken(token)).isEqualTo(email);

            Instant expiration = tokenProvider.getExpirationFromToken(token);
            assertThat(expiration).isAfter(Instant.now());
        }

        @Test
        @DisplayName("Should reject tampered token")
        void shouldRejectTamperedToken() {
            String token = tokenProvider.generateToken(1L, "user@example.com");
            String[] parts = token.split("\\.");

            // Tamper with payload
            String tamperedPayload = parts[1] + "tamper";
            String tamperedToken = parts[0] + "." + tamperedPayload + "." + parts[2];

            assertThat(tokenProvider.validateToken(tamperedToken)).isFalse();
            assertThatThrownBy(() -> tokenProvider.getUserIdFromToken(tamperedToken))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should reject expired token")
        void shouldRejectExpiredToken() {
            // Expired 5 seconds ago
            String token = tokenProvider.generateToken(10L, "expired@example.com", -5000L);

            assertThat(tokenProvider.validateToken(token)).isFalse();
            assertThatThrownBy(() -> tokenProvider.getUserIdFromToken(token))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should reject invalid or null token strings")
        void shouldRejectInvalidStrings() {
            assertThat(tokenProvider.validateToken(null)).isFalse();
            assertThat(tokenProvider.validateToken("")).isFalse();
            assertThat(tokenProvider.validateToken("invalid.token")).isFalse();
            assertThat(tokenProvider.validateToken("not.a.valid.jwt.token")).isFalse();
        }
    }

    @Nested
    @DisplayName("UserPrincipal Tests")
    class UserPrincipalTests {

        @Test
        @DisplayName("Should create UserPrincipal from User entity with ROLE_USER")
        void shouldCreateFromUserEntity() {
            User user = new User("Jane Doe", "jane@example.com", "$2a$12$somePasswordHash");
            user.setId(99L);

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal.getId()).isEqualTo(99L);
            assertThat(principal.getUsername()).isEqualTo("jane@example.com");
            assertThat(principal.getEmail()).isEqualTo("jane@example.com");
            assertThat(principal.getPassword()).isEqualTo("$2a$12$somePasswordHash");
            assertThat(principal.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
            assertThat(principal.isEnabled()).isTrue();
            assertThat(principal.isAccountNonLocked()).isTrue();
            assertThat(principal.isAccountNonExpired()).isTrue();
            assertThat(principal.isCredentialsNonExpired()).isTrue();
        }
    }
}
