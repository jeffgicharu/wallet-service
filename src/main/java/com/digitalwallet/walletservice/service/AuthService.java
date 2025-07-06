
package com.digitalwallet.walletservice.service;

import com.digitalwallet.walletservice.model.Account;
import com.digitalwallet.walletservice.model.User;
import com.digitalwallet.walletservice.repository.UserRepository;
import com.digitalwallet.walletservice.model.AuthPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager; // Changed to field injection

    // Update constructor to remove AuthenticationManager
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public CompletableFuture<AuthPayload> registerUser(String username, String phoneNumber, String pin) {
        CompletableFuture<AuthPayload> future = new CompletableFuture<>();

        // Create and save the new user in a transaction
        User savedUser = createNewUser(username, phoneNumber, pin);

        // Register a callback to run after the transaction commits
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // Generate JWT only after the user is committed
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        savedUser.getPhoneNumber(),
                        savedUser.getPin(),
                        new ArrayList<>()
                );
                String token = jwtService.generateToken(userDetails);
                future.complete(new AuthPayload(token, savedUser));
            }
        });

        return future;
    }

    @Transactional
    public User createNewUser(String username, String phoneNumber, String pin) {
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalArgumentException("User with phone number " + phoneNumber + " already exists");
        }

        String hashedPassword = passwordEncoder.encode(pin);

        User user = User.builder()
                .username(username)
                .phoneNumber(phoneNumber)
                .pin(hashedPassword)
                .build();

        Account account = Account.builder()
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        user.setAccount(account);
        return userRepository.save(user);
    }

    public AuthPayload login(String phoneNumber, String pin) {
        // 1. Authenticate the user. An exception is thrown if it fails.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(phoneNumber, pin)
        );

        // 2. Find the user
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Login failed for user: " + phoneNumber));

        // 3. Generate a new JWT
        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), user.getPin(), new ArrayList<>()));

        // 4. Return the AuthPayload
        return new AuthPayload(token, user);
    }
}
