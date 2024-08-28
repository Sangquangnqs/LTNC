package com.project.backend.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.project.backend.exceptionhandler.ExceptionLog;

/**
 * Configures the security filter chain for the application.
 * This method sets up the following security settings:
 * - Adds the JwtAuthenticationFilter before the
 * UsernamePasswordAuthenticationFilter
 * - Disables CSRF protection
 * - Authorizes requests to "/student/**" for users with the "STUDENT" authority
 * - Authorizes requests to "/teacher/**" for users with the "TEACHER" authority
 * - Permits all other requests
 * - Disables logout functionality
 * - Sets the session management policy to STATELESS, which means no session
 * will be created or used.
 *
 * @param http the HttpSecurity object to configure the security settings
 * @return the configured SecurityFilterChain
 * @throws Exception if an error occurs during the configuration
 */
@Configuration
@EnableWebSecurity
public class BackendSecurityConfiguration {
    /**
     * The JwtAuthenticationFilter is an autowired dependency that is used to filter
     * incoming requests and authenticate them using a JSON Web Token (JWT).
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    /**
     * A PasswordEncoder implementation that uses the BCrypt hashing algorithm to
     * securely
     * store and compare passwords. This encoder is used throughout the application
     * to
     * handle password-related operations.
     */
    public static final PasswordEncoder encoder = new BCryptPasswordEncoder();
    /**
     * The BackendDetailsService is an autowired dependency that is used to retrieve
     * user details for authentication purposes.
     */
    @Autowired
    private BackendDetailsService service;
    /**
     * An autowired dependency that is used to log exceptions that occur during the
     * application's execution.
     */
    @Autowired
    private ExceptionLog exceptionLog;

    /**
     * Configures the security filter chain for the application.
     * This method sets up the following security settings:
     * - Adds the JwtAuthenticationFilter before the
     * UsernamePasswordAuthenticationFilter
     * - Disables CSRF protection
     * - Authorizes requests to "/student/**" for users with the "STUDENT" authority
     * - Authorizes requests to "/teacher/**" for users with the "TEACHER" authority
     * - Permits all other requests
     * - Disables logout functionality
     * - Sets the session management policy to STATELESS, which means no session
     * will be created or used.
     *
     * @param http the HttpSecurity object to configure the security settings
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during the configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                    req -> req.requestMatchers("/teacher/all", "/course/**")
                              .authenticated()
                              .requestMatchers("/teacher/**")
                              .hasAuthority("TEACHER")
                              .requestMatchers("/student/**")
                              .hasAuthority("STUDENT")
                              .anyRequest()
                              .permitAll()
                )
                .logout(out -> out.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    /**
     * Configures the authentication manager for the application.
     * This method sets up the authentication provider that is used to authenticate
     * users.
     * The authentication provider is configured with the password encoder, the
     * backend details service,
     * and the exception log.
     *
     * @param http the HttpSecurity object to configure the authentication manager
     * @return the configured AuthenticationManager
     * @throws Exception if an error occurs during the configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(
                new BackendAuthenticationProvider(BackendSecurityConfiguration.encoder, service, exceptionLog));
        return builder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
