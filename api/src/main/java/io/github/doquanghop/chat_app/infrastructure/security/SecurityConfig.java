package io.github.doquanghop.chat_app.infrastructure.security;

import io.github.doquanghop.chat_app.application.fliter.JwtAuthenticationFilter;
import io.github.doquanghop.chat_app.application.fliter.RequestIdFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RequestIdFilter requestIdFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(requestIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/api/v1/account/login",
                                "/api/v1/account/register",
                                "/api/v1/account/refreshToken",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/ws/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

}
