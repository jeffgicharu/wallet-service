
package com.digitalwallet.walletservice.service;

import com.digitalwallet.walletservice.model.Account;
import com.digitalwallet.walletservice.model.User;
import com.digitalwallet.walletservice.repository.UserRepository;
import com.digitalwallet.walletservice.model.AuthPayload; // We will create this DTO
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final AuthenticationManager authenticationManager; // Add this

    // Update the constructor
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager; // Add this
    }

    @Transactional
    public AuthPayload registerUser(String username, String phoneNumber, String pin) {
        // 1. Hash the incoming PIN
        String hashedPassword = passwordEncoder.encode(pin);

        // 2. Create the User entity
        User user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setPin(hashedPassword);


        // 3. Create the associated Account
        Account account = new Account();
        account.setBalance(BigDecimal.ZERO); // Set initial balance to 0
        account.setUser(user);

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

    public AuthPayload login(String phoneNumber, String pin) {
        // 1. Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(phoneNumber, pin)
        );

        // If authentication is successful, proceed. Otherwise, an exception is thrown.
        // 2. Find the user
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Login failed for user: " + phoneNumber));

        // 3. Generate a new JWT
        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), user.getPin(), new ArrayList<>()));

        // 4. Return the AuthPayload
        return new AuthPayload(token, user);
    }
}
