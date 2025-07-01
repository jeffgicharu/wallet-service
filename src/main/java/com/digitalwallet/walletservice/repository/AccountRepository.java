package com.digitalwallet.walletservice.repository;

import com.digitalwallet.walletservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

 @Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}