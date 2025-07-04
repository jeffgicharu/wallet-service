package com.digitalwallet.walletservice.controller;

import com.digitalwallet.walletservice.model.Transaction;
import com.digitalwallet.walletservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()") // This secures the mutation
    public Transaction transferFunds(
            @Argument String receiverPhoneNumber,
            @Argument BigDecimal amount,
            Authentication authentication) { // Spring provides the authenticated user
        
        String senderPhoneNumber = authentication.getName(); // The user's name is their phone number
        return transactionService.transferFunds(senderPhoneNumber, receiverPhoneNumber, amount);
    }
}
