package com.lifeos.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider {

    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final String ENCODED_HEADER = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(HEADER_JSON.getBytes(StandardCharsets.UTF_8));

    private final byte[] secretKeyBytes;
    private final long defaultExpirationMs;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public TokenProvider(
            @Value("${app.security.jwt.secret:default-lifeos-secret-key-that-is-at-least-256-bits-long-for-hmac-sha256-signing!}") String secret,
            @Value("${app.security.jwt.expiration-ms:604800000}") long defaultExpirationMs
    ) {
        this.secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.defaultExpirationMs = defaultExpirationMs;
        this.objectMapper = new ObjectMapper();
    }

    public TokenProvider(String secret, long defaultExpirationMs, ObjectMapper objectMapper) {
        this.secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.defaultExpirationMs = defaultExpirationMs;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    public String generateToken(Long userId, String email) {
        return generateToken(userId, email, this.defaultExpirationMs);
    }

    public String generateToken(Long userId, String email, long expirationMs) {
        try {
            Instant now = Instant.now();
            Instant expiration = now.plusMillis(expirationMs);

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("userId", userId);
            payloadMap.put("email", email);
            payloadMap.put("iat", now.getEpochSecond());
            payloadMap.put("exp", expiration.getEpochSecond());

            String payloadJson = objectMapper.writeValueAsString(payloadMap);
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

            String contentToSign = ENCODED_HEADER + "." + encodedPayload;
            String signature = sign(contentToSign);

            return contentToSign + "." + signature;
        } catch (Exception e) {
            log.error("Failed to generate token for user {}", userId, e);
            throw new IllegalStateException("Could not generate authentication token", e);
        }
    }

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }

        try {
            String contentToSign = parts[0] + "." + parts[1];
            String expectedSignature = sign(contentToSign);

            if (!MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                log.warn("Token signature validation failed");
                return false;
            }

            Map<String, Object> claims = parseClaims(parts[1]);
            Number expNumber = (Number) claims.get("exp");
            if (expNumber == null) {
                return false;
            }

            long expSeconds = expNumber.longValue();
            if (Instant.now().getEpochSecond() >= expSeconds) {
                log.warn("Token has expired");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Map<String, Object> claims = getValidatedClaims(token);
        Number userId = (Number) claims.get("userId");
        return userId != null ? userId.longValue() : null;
    }

    public String getEmailFromToken(String token) {
        Map<String, Object> claims = getValidatedClaims(token);
        return (String) claims.get("email");
    }

    public Instant getExpirationFromToken(String token) {
        Map<String, Object> claims = getValidatedClaims(token);
        Number exp = (Number) claims.get("exp");
        return exp != null ? Instant.ofEpochSecond(exp.longValue()) : null;
    }

    private Map<String, Object> getValidatedClaims(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        String[] parts = token.split("\\.");
        return parseClaims(parts[1]);
    }

    private Map<String, Object> parseClaims(String encodedPayload) {
        try {
            byte[] jsonBytes = Base64.getUrlDecoder().decode(encodedPayload);
            return objectMapper.readValue(jsonBytes, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode token payload", e);
        }
    }

    private String sign(String content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, HMAC_SHA256);
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }
}
