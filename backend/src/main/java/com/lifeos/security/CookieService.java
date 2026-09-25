package com.lifeos.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieService {

    private final String cookieName;
    private final boolean secure;
    private final String sameSite;
    private final long expirationMs;

    public CookieService(
            @Value("${app.security.cookie.name:lifeos_token}") String cookieName,
            @Value("${app.security.cookie.secure:false}") boolean secure,
            @Value("${app.security.cookie.same-site:Lax}") String sameSite,
            @Value("${app.security.jwt.expiration-ms:604800000}") long expirationMs
    ) {
        this.cookieName = cookieName;
        this.secure = secure;
        this.sameSite = sameSite;
        this.expirationMs = expirationMs;
    }

    public void addAuthCookie(HttpServletResponse response, String token) {
        long maxAgeSeconds = expirationMs / 1000;
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearAuthCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<Cookie> authCookie = Arrays.stream(request.getCookies())
                    .filter(c -> cookieName.equals(c.getName()))
                    .findFirst();
            if (authCookie.isPresent() && authCookie.get().getValue() != null && !authCookie.get().getValue().isBlank()) {
                return authCookie.get().getValue();
            }
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }

        return null;
    }

    public String getCookieName() {
        return cookieName;
    }

    public boolean isSecure() {
        return secure;
    }
}
