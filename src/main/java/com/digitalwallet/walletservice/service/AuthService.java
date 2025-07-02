
package com.digitalwallet.walletservice.service;

import com.digitalwallet.walletservice.model.Account;
import com.digitalwallet.walletservice.model.User;
import com.digitalwallet.walletservice.repository.UserRepository;
import com.digitalwallet.walletservice.model.AuthPayload; // We will create this DTO
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthPayload registerUser(String username, String phoneNumber, String pin) {
        // 1. Hash the incoming PIN
        String hashedPassword = passwordEncoder.encode(pin);

        // 2. Create the User entity
        User user = User.builder()
                .username(username)
                .phoneNumber(phoneNumber)
                .pin(hashedPassword)
                .build();

        // 3. Create the associated Account
        Account account = Account.builder()
                .balance(BigDecimal.ZERO) // Set initial balance to 0
                .user(user)
                .build();

        // 4. Link the account to the user
        user.setAccount(account);

        // 5. Save the user (which also saves the account due to cascading)
        User savedUser = userRepository.save(user);

        // 6. Generate a JWT for the new user
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            savedUser.getPhoneNumber(),
            savedUser.getPin(),
            new ArrayList<>()
        );
        String token = jwtService.generateToken(userDetails);

        // 7. Return the AuthPayload
        return new AuthPayload(token, savedUser);
    }
}
