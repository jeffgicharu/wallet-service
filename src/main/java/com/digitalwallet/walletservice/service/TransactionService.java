package com.digitalwallet.walletservice.service;

import com.digitalwallet.walletservice.model.Account;
import com.digitalwallet.walletservice.model.Transaction;
import com.digitalwallet.walletservice.model.TransactionType;
import com.digitalwallet.walletservice.model.User;
import com.digitalwallet.walletservice.repository.TransactionRepository;
import com.digitalwallet.walletservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction transferFunds(String senderPhoneNumber, String receiverPhoneNumber, BigDecimal amount) {
        // 1. Get sender and receiver user objects
        User sender = userRepository.findByPhoneNumber(senderPhoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));
        User receiver = userRepository.findByPhoneNumber(receiverPhoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Receiver not found"));

        Account senderAccount = sender.getAccount();
        Account receiverAccount = receiver.getAccount();

        // 2. Validate sufficient balance
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds for transfer");
        }

        // 3. Perform debit and credit
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        // 4. Record both DEBIT and CREDIT transactions
        Transaction senderTransaction = Transaction.builder()
                .account(senderAccount)
                .amount(amount)
                .type(TransactionType.DEBIT)
                .description("Transfer to " + receiver.getUsername())
                .sender(sender)
                .receiver(receiver)
                .timestamp(Instant.now())
                .build();

        Transaction receiverTransaction = Transaction.builder()
                .account(receiverAccount)
                .amount(amount)
                .type(TransactionType.CREDIT)
                .description("Transfer from " + sender.getUsername())
                .sender(sender)
                .receiver(receiver)
                .timestamp(Instant.now())
                .build();

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);
        
        // 5. Return the sender's transaction object
        return senderTransaction;
    }
}
