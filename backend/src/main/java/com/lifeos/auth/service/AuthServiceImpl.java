package com.lifeos.auth.service;

import com.lifeos.auth.dto.LoginRequest;
import com.lifeos.auth.dto.RegisterRequest;
import com.lifeos.auth.dto.UserResponse;
import com.lifeos.common.exception.EmailAlreadyExistsException;
import com.lifeos.common.exception.InvalidCredentialsException;
import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.common.exception.UnauthorizedException;
import com.lifeos.security.CookieService;
import com.lifeos.security.TokenProvider;
import com.lifeos.security.UserPrincipal;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CookieService cookieService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider,
            CookieService cookieService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.cookieService = cookieService;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request, HttpServletResponse response) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedName = request.getName().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = new User(normalizedName, normalizedEmail, passwordHash);
        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail());
        cookieService.addAuthCookie(response, token);

        return UserResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request, HttpServletResponse response) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getId(), user.getEmail());
        cookieService.addAuthCookie(response, token);

        return UserResponse.fromEntity(user);
    }

    @Override
    public void logout(HttpServletResponse response) {
        cookieService.clearAuthCookie(response);
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new UnauthorizedException("Authentication required to access current user");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + principal.getId()));

        return UserResponse.fromEntity(user);
    }
}
