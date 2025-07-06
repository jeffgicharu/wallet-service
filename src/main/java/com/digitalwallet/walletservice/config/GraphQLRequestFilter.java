package com.digitalwallet.walletservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class GraphQLRequestFilter extends OncePerRequestFilter {

    public static final String IS_PUBLIC_GRAPHQL_MUTATION = "isPublicGraphQLMutation";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Only process GraphQL POST requests
        if (request.getRequestURI().equals("/graphql") && request.getMethod().equalsIgnoreCase("POST")) {
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            String requestBody = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());

            if (requestBody.contains("registerUser") || requestBody.contains("login")) {
                requestWrapper.setAttribute(IS_PUBLIC_GRAPHQL_MUTATION, true);
            }
            filterChain.doFilter(requestWrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
