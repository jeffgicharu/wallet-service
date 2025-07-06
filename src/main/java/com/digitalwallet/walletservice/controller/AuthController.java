
package com.digitalwallet.walletservice.controller;

import com.digitalwallet.walletservice.model.AuthPayload;
import com.digitalwallet.walletservice.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @MutationMapping
    public CompletableFuture<AuthPayload> registerUser(@Argument String username, @Argument String phoneNumber, @Argument String pin) {
        return authService.registerUser(username, phoneNumber, pin);
    }

    @MutationMapping
    public AuthPayload login( @Argument String phoneNumber, @Argument String pin) {
        return authService.login(phoneNumber, pin);
    }
}
