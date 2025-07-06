package com.digitalwallet.walletservice.controller;

import com.digitalwallet.walletservice.model.User;
import com.digitalwallet.walletservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller 
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @QueryMapping 
    @PreAuthorize("isAuthenticated()") // Secures this endpoint
    public User me(Authentication authentication) {
        // The user's phone number is the 'name' in the Authentication principal
        String phoneNumber = authentication.getName();
        return userService.getUserByPhoneNumber(phoneNumber);
    }
}