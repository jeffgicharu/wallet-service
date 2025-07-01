package com.digitalwallet.walletservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

 @Data @Builder @NoArgsConstructor @AllArgsConstructor @Entity @Table(name = "transactions")
public class Transaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // Corrected here
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY) // Corrected here
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY) // And corrected here
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @CreationTimestamp
    private Instant timestamp;
}