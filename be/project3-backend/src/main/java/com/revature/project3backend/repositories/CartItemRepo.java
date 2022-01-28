package com.revature.project3backend.repositories;

import com.revature.project3backend.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CartItemRepo is the repository for cart items
 */
public interface CartItemRepo extends JpaRepository <CartItem, Integer> {
}
