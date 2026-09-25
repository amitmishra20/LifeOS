package com.lifeos.auth.service;

import com.lifeos.auth.dto.LoginRequest;
import com.lifeos.auth.dto.RegisterRequest;
import com.lifeos.auth.dto.UserResponse;
import com.lifeos.security.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request, HttpServletResponse response);

    UserResponse login(LoginRequest request, HttpServletResponse response);

    void logout(HttpServletResponse response);

    UserResponse getCurrentUser(UserPrincipal principal);
}
