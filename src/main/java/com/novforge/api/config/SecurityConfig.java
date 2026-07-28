package com.novforge.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] MOTHERBOARD_API_PATHS = {
            "/api/mainboards/**",
            "/api/motherboards/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers(MOTHERBOARD_API_PATHS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(MOTHERBOARD_API_PATHS).permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }
}
