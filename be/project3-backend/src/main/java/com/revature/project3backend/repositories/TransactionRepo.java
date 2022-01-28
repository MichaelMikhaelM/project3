package com.revature.project3backend.repositories;

import com.revature.project3backend.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TransactionRepo is the repository for transactions
 */
public interface TransactionRepo extends JpaRepository <Transaction, Integer> {
}
