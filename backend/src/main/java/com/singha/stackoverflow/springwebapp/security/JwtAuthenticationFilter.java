package com.singha.stackoverflow.springwebapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Read 'Authorization' Header out of the request payload packet
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extract token past "Bearer " prefix
            String username = jwtUtils.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // For a simple setup, we mock up a UserDetails object using the extracted name
                var authorities = java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"));

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "", authorities);

                if (jwtUtils.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // Log user into Spring Security context framework memory
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // Hand execution down to the next filter node in line
        filterChain.doFilter(request, response);
    }
}