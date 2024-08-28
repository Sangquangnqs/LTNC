package com.project.backend.security;


import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.project.backend.exceptionhandler.ExceptionLog;

public class BackendAuthenticationProvider implements AuthenticationProvider {
    // Password encoder for hashing passwords
    private final PasswordEncoder encoder;
    // Service for loading user details by email
    private final BackendDetailsService service;
    // Logging service for exceptions
    private final ExceptionLog exceptionLog;

    // Constructor to initialize the provider with a password encoder, user details service, and exception logging service
    public BackendAuthenticationProvider(PasswordEncoder encoder, 
                                         BackendDetailsService service, 
                                         ExceptionLog exceptionLog){
        this.encoder = encoder;
        this.exceptionLog = exceptionLog;
        this.service = service;
    }

    // Override the authenticate method to authenticate a user based on their email and password
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Extract the email from the authentication object
        String email = authentication.getName();
        // Load user details by email
        AuthenticationDetails details = service.loadUserByEmail(email);
        // If user details are not found, log the exception and return the authentication object marked as not authenticated
        if (details == null) {
            exceptionLog.log(new UsernameNotFoundException(this.getClass().getName() + ": " + authentication.toString()));
            authentication.setAuthenticated(false);
            return authentication;
        }
        // Extract the password from the authentication object
        String password = authentication.getCredentials()
                                        .toString();
        // Compare the provided password with the stored password
        String userPassword = details.getPassword();
        String role = authentication.getAuthorities()
                                    .iterator()
                                    .next()
                                    .toString();
        // If the passwords match, create an authenticated UsernamePasswordAuthenticationToken with the user's details and roles
        if (encoder.matches(password, userPassword) && details.getRole().equals(role)) {
            List<SimpleGrantedAuthority> list = List.of(new SimpleGrantedAuthority(details.getRole()));
            return UsernamePasswordAuthenticationToken.authenticated(details.getId(), userPassword, list);
        } else {
            // If the passwords do not match, log the exception and return the authentication object marked as not authenticated
            exceptionLog.log(new UsernameNotFoundException(this.getClass().getName() + ": " + authentication.toString()));
            authentication.setAuthenticated(false);
            return authentication;
        }
    }

    // Override the supports method to indicate that this provider supports UsernamePasswordAuthenticationToken
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}