package com.lifeos.user.controller;

import com.lifeos.security.CookieService;
import com.lifeos.security.UserPrincipal;
import com.lifeos.user.dto.UserProfileSummaryResponse;
import com.lifeos.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final CookieService cookieService;

    public UserController(UserService userService, CookieService cookieService) {
        this.userService = userService;
        this.cookieService = cookieService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileSummaryResponse> getProfileSummary(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserProfileSummaryResponse response = userService.getProfileSummary(principal.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response
    ) {
        // 1. Transactional deletion of user and all cascaded data
        userService.deleteCurrentUser(principal.getId());

        // 2. Only after successful transactional deletion, clear authentication cookie
        cookieService.clearAuthCookie(response);

        // 3. Clear security context
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("message", "Account and all associated data have been permanently deleted."));
    }
}
