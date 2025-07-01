package com.digitalwallet.walletservice.repository;

import com.digitalwallet.walletservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

 @Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}