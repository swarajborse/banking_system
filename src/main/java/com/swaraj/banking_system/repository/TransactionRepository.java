package com.swaraj.banking_system.repository;

import com.swaraj.banking_system.entity.BankAccount;
import com.swaraj.banking_system.entity.Transaction;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findBySenderAccountOrReceiverAccountOrderByCreatedAtDesc(
            BankAccount senderAccount,
            BankAccount receiverAccount,
            Pageable pageable
    );
}
