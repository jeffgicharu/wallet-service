package com.digitalwallet.walletservice.service;

import com.digitalwallet.walletservice.model.User;
import com.digitalwallet.walletservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service 
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByPhoneNumber(String phoneNumber) {
        // Fetch the user from the database. The account and other details will be fetched
        // automatically due to the relationships we've defined.
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));
    }
}
