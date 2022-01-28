package com.revature.project3backend.services;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.models.CartItem;
import com.revature.project3backend.repositories.CartItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * CartItemService contains the Service layer methods for cart items
 */
@Service
@Transactional
public class CartItemService {
	/**
	 * The instance of CartItemRepo to use
	 */
	private final CartItemRepo cartItemRepo;
	
	/**
	 * This constructor is automatically called by Spring
	 *
	 * @param cartItemRepo The instance of CartItemRepo to use
	 */
	@Autowired
	public CartItemService (CartItemRepo cartItemRepo) {
		this.cartItemRepo = cartItemRepo;
	}
	
	/**
	 * Saves a cart item to the database
	 *
	 * @param cartItem The cart item to save to the database
	 */
	public void createCartItem (CartItem cartItem) {
		this.cartItemRepo.save (cartItem);
	}
	
	/**
	 * Updates a cart item's quantity
	 *
	 * @param cartItemId The id of the cart item to update
	 * @param quantity The value to set the cart item's quantity to
	 * @throws InvalidValueException Thrown when cart item cannot be found with given id
	 */
	public void updateCartItem (Integer cartItemId, Integer quantity) throws InvalidValueException {
		CartItem cartItem = cartItemRepo.findById (cartItemId).orElse (null);
		
		if (cartItem == null) {
			throw new InvalidValueException ("Invalid cart item id");
		}
		
		cartItem.setQuantity (quantity);
		
		cartItemRepo.save (cartItem);
	}
	
	/**
	 * Delete a cart item from the database
	 *
	 * @param cartItemId The id of the cart item to delete
	 */
	public void deleteCartItem (Integer cartItemId) {
		this.cartItemRepo.deleteById (cartItemId);
	}
}
