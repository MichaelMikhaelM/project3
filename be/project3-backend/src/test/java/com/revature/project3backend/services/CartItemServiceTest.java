package com.revature.project3backend.services;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.models.CartItem;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.models.User;
import com.revature.project3backend.models.UserRole;
import com.revature.project3backend.repositories.CartItemRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CartItemServiceTest {
	private final CartItemRepo cartItemRepo = Mockito.mock (CartItemRepo.class);
	private final CartItemService cartItemService;
	
	List <Product> products = new ArrayList <> ();
	List <User> users = new ArrayList <> ();
	
	public CartItemServiceTest () {
		cartItemService = new CartItemService (cartItemRepo);
	}
	
	@BeforeEach
	void setUp () {
		products.add (new Product (1, "Java I", "A beginner Java course", 10.00F, "", 8.00F, 5));
		products.add (new Product (2, "Java II", "An intermediate Java course", 20.00F, "", 18.00F, 5));
		products.add (new Product (3, "Python I", "A beginner Python course", 10.00F, "", 8.00F, 5));
		products.add (new Product (4, "Python II", "An intermediate Python course", 20.00F, "", 18.00F, 5));
		
		users.add (new User (1, "User", "1", "email1", "username13123", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER")));
		users.add (new User (2, "User", "2", "email2", "username22425", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER")));
		users.add (new User (3, "User", "3", "email3", "username32323", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER")));
	}
	
	@AfterEach
	void tearDown () {
		products.clear ();
		users.clear ();
	}
	
	@Test
	void createCartItem () {
		CartItem cartItem = new CartItem (users.get (1), products.get (2), 4);
		
		cartItemService.createCartItem (cartItem);
		
		Mockito.verify (this.cartItemRepo).save (cartItem);
	}
	
	@Test
	void updateCartItem () throws InvalidValueException {
		int id = 1;
		int quantity = 3;
		int newQuantity = 4;
		
		CartItem cartItem = new CartItem (id, users.get (0), products.get (1), quantity);
		
		Mockito.when (cartItemRepo.findById (id)).thenReturn (Optional.of (cartItem));
		
		cartItemService.updateCartItem (id, newQuantity);
		
		assertEquals (newQuantity, cartItem.getQuantity ());
		
		Mockito.verify (cartItemRepo).save (cartItem);
	}
	
	@Test
	void updateCartItemWhenNotFound () {
		int id = 1;
		
		Mockito.when (this.cartItemRepo.findById (id)).thenReturn (Optional.empty ());
		
		InvalidValueException exception = assertThrows (InvalidValueException.class, () -> cartItemService.updateCartItem (id, 1));
		
		assertEquals ("Error! Invalid cart item id", exception.getMessage ());
	}
	
	@Test
	void deleteCartItem () {
		int id = 1;
		
		cartItemService.deleteCartItem (id);
		
		Mockito.verify (cartItemRepo).deleteById (id);
	}
}