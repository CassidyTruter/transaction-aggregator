package com.fintech.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
public class BasicAuthSecurity {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RateLimitingFilter rateLimitingFilter;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Autowired
    public BasicAuthSecurity(RestAuthenticationEntryPoint authenticationEntryPoint,
                             RateLimitingFilter rateLimitingFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
        );

        http.httpBasic(basic -> basic
                .authenticationEntryPoint(authenticationEntryPoint)
        );

        http.headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                        .policyDirectives("default-src 'self'; frame-ancestors 'none';")
                )
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss
                        .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentTypeOptions(contentType -> contentType.disable())
        );

        if (sslEnabled) {
            http.requiresChannel(channel -> channel
                    .anyRequest().requiresSecure()
            );
        }

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Changed to STATELESS for API
        );

        http.addFilterBefore(rateLimitingFilter,
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        // This still uses in-memory authentication which is not suitable for production banking. For production,
        // replace with database-backed authentication or an enterprise identity provider.
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder)
                .withUser(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN");

        // TODO: Replace with database authentication:
        // auth.jdbcAuthentication()
        //     .dataSource(dataSource)
        //     .passwordEncoder(passwordEncoder);
    }
}
