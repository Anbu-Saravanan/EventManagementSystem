package com.anbu.ems.config;

import com.anbu.ems.model.Role;
import com.anbu.ems.model.User;
import com.anbu.ems.repository.UserRepository;
import com.anbu.ems.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    // ---- Authentication Provider & Manager ----

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(customUserDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
            throws Exception {
        return cfg.getAuthenticationManager();
    }

    // ---- 1) Filter chain for refresh (no resource-server) ----

    @Bean
    @Order(1)
    public SecurityFilterChain refreshChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .securityMatcher("/api/auth/refresh")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a.anyRequest().permitAll());
        return http.build();
    }

    // Chain 2: main API security
    @Bean
    @Order(2)
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/auth/register").permitAll()
                        .requestMatchers("/api/events/**", "/api/users/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/attendance/**").hasRole("ADMIN")
                        .requestMatchers("/api/registration/**").permitAll()
                        .requestMatchers("/api/speakers/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    // ---- JWT Decoder (HS256) ----

//    @Bean
//    public JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret) {
//        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
//        // if you store the secret Base64-encoded, use:
//        // byte[] keyBytes = Base64.getDecoder().decode(secret);
//        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
//        return NimbusJwtDecoder.withSecretKey(key).build();
//    }

    // ---- JWT → GrantedAuthority converter ----

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter conv = new JwtGrantedAuthoritiesConverter();
        conv.setAuthoritiesClaimName("role");  // your “role” claim
        conv.setAuthorityPrefix("");           // because it already carries “ROLE_*”

        JwtAuthenticationConverter jc = new JwtAuthenticationConverter();
        jc.setJwtGrantedAuthoritiesConverter(conv);
        return jc;
    }

    // ---- Global CORS configuration ----

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:3000",
                "https://eventmanagementsystem-react-fronten.vercel.app"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public CommandLineRunner createDefaultAdmin() {
        return args -> {
            String adminEmail = "anbu.saravanan11211@gmail.com";

            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setUsername("Jeevenandhan");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder().encode("admin")); // strong password
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);
                System.out.println("Default admin created: admin@ems.com / admin123");
            }
        };
    }
}
