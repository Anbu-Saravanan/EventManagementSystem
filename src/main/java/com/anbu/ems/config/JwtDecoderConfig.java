package com.anbu.ems.config;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtDecoderConfig {

    // Pull in the same secret you use in your JwtService
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtDecoder jwtDecoder() {
        // If your secret is Base64-encoded, first do Base64.getDecoder().decode(jwtSecret)
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        // Build a SecretKeySpec for HS256
        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");

        // Create a NimbusJwtDecoder with that key
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
