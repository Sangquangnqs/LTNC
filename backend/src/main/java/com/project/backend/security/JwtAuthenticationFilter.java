package com.project.backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.cloud.firestore.DocumentSnapshot;
import com.project.backend.exceptionhandler.ExceptionLog;
import com.project.backend.repository.FirestoreRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A Spring Security filter that handles JWT-based authentication for incoming
 * requests.
 * 
 * This filter is responsible for:
 * - Checking if the request URI is in the ignored path list (e.g. "/login")
 * - Extracting the JWT token from the "Authorization" header
 * - Decoding and validating the JWT token
 * - Retrieving the user details from Firestore based on the token
 * - Setting the authenticated user context in the Spring Security context
 * 
 * If the token is missing, invalid, or the user details cannot be retrieved,
 * the filter will
 * send a 401 Unauthorized response.
 */
@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private FirestoreRepository repository;
    @Autowired
    private ExceptionLog exceptionLog;
    @Autowired
    private JwtUtils jwtUtils;
    private final List<String> igoredPath = List.of("/login", "/validate", "/register");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (igoredPath.contains(request.getRequestURI())) {
            doFilter(request, response, filterChain);
            return;
        }
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            exceptionLog.log(new IOException(this.getClass().getName()));
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        token = token.split(" ")[1].trim();
        Claims claims = jwtUtils.decodeToken(token);
        if (!jwtUtils.isValid(claims)) {
            exceptionLog.log(new IOException(this.getClass().getName()));
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        String id = jwtUtils.getId(claims);
        String role = jwtUtils.getRole(claims);
        String password = jwtUtils.getPassword(claims);
        DocumentSnapshot snapshot = repository.getDocumentById(AuthenticationDetails.class, id);
        if (snapshot == null) {
            exceptionLog.log(new IOException(this.getClass().getName()));
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        String userPassword = snapshot.get("password", String.class);
        if (BackendSecurityConfiguration.encoder.matches(password, userPassword)) {
            Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(id,
                    userPassword,
                    List.of(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            doFilter(request, response, filterChain);
        } else {
            exceptionLog.log(new IOException(this.getClass().getName()));
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }
}