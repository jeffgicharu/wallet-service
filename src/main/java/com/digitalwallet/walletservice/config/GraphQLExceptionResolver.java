package com.digitalwallet.walletservice.config;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    public List<GraphQLError> resolveToMultipleErrors(Throwable ex, DataFetchingEnvironment dfe) {
        if (ex instanceof IllegalArgumentException) {
            // For user registration errors (e.g., user already exists)
            return Collections.singletonList(GraphQLError.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .build());
        } else if (ex instanceof AuthenticationException) {
            // For authentication errors (e.g., bad credentials)
            String errorMessage = "Authentication failed: Invalid credentials.";
            if (ex instanceof BadCredentialsException) {
                errorMessage = "Incorrect phone number or PIN.";
            }
            // Log the full exception for backend debugging
            logger.warn("Authentication failed: " + ex.getMessage(), ex);
            return Collections.singletonList(GraphQLError.newError()
                    .errorType(ErrorType.UNAUTHORIZED)
                    .message(errorMessage)
                    .build());
        }
        // For any other unexpected exceptions, return a generic error and log the full stack trace
        logger.error("Unhandled exception during GraphQL execution: " + ex.getMessage(), ex);
        return Collections.singletonList(GraphQLError.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An unexpected error occurred.")
                .build());
    }
}
