package com.revature.project3backend.services;

import com.revature.project3backend.exceptions.InvalidCredentialsException;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.models.CartItem;
import com.revature.project3backend.models.User;
import com.revature.project3backend.repositories.CartItemRepo;
import com.revature.project3backend.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * UserService contains the Service layer methods for users
 */
@Service
@Transactional
public class UserService {
	/**
	 * The instance of UserRepo to use
	 */
	private final UserRepo userRepo;
	
	/**
	 * The instance of CartItemRepo to use
	 */
	private final CartItemRepo cartItemRepo;
	
	/**
	 * The instance of BCryptPasswordEncoder to use to encrypt passwords
	 */
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder ();
	
	/**
	 * This constructor is automatically called by Spring
	 *
	 * @param userRepo The instance of UserRepo to use
	 * @param cartItemRepo The instance of CartItemRepo to use
	 */
	@Autowired
	public UserService (UserRepo userRepo, CartItemRepo cartItemRepo) {
		this.userRepo = userRepo;
		this.cartItemRepo = cartItemRepo;
	}
	
	/**
	 * Throws an error if the given username and password do not match with a user
	 *
	 * @param username The username to log in with
	 * @param password The password to log in with
	 * @return The user that matches the given username and password
	 * @throws InvalidCredentialsException Thrown when the username doesn't match a user or when the password is incorrect for the given username
	 */
	public User loginUser (String username, String password) throws InvalidCredentialsException {
		User user = userRepo.findByUsername (username);
		
		//if no user was found with username
		if (user == null) {
			throw new InvalidCredentialsException ();
		}
		
		if (passwordEncoder.matches (password, user.getPassword ())) {
			return user;
		} else {
			throw new InvalidCredentialsException ();
		}
	}
	
	/**
	 * Validates and then creates a user
	 *
	 * @param user The user to create
	 * @return The created user
	 * @throws InvalidValueException Thrown when validation fails
	 */
	public User createUser (User user) throws InvalidValueException {
		User userWithUsername = userRepo.findByUsername (user.getUsername ());
		
		//if user with username exists
		if (userWithUsername != null) {
			throw new InvalidValueException ("Username already in use");
		}
		
		User userWithEmail = userRepo.findByEmail (user.getEmail ());
		
		//if user with email exists
		if (userWithEmail != null) {
			throw new InvalidValueException ("Email already in use");
		}
		
		//encrypt password
		user.setPassword (passwordEncoder.encode (user.getPassword ()));
		
		return userRepo.save (user);
	}
	
	/**
	 * Adds an item to the user's cart
	 *
	 * @param user The user who owns the cart that the cart item will be added to
	 * @param cartItem The cart item to add to the user's cart
	 */
	public void addToCart (User user, CartItem cartItem) {
		user.getCart ().add (cartItem);
		
		userRepo.save (user);
	}
	
	/**
	 * Removes an item from the user's cart
	 *
	 * @param user The user who owns the cart that the cart item will be removed from
	 * @param cartItem The cart item to remove from the user's cart
	 */
	public void removeFromCart (User user, CartItem cartItem) {
		user.getCart ().remove (cartItem);
		
		cartItemRepo.delete (cartItem);
		
		userRepo.save (user);
	}
	
	/**
	 * Removes all items from the user's cart
	 *
	 * @param user The user who owns the cart that all cart items will be removed from
	 */
	public void clearCart (User user) {
		List <CartItem> cart = user.getCart ();
		
		user.setCart (new ArrayList <> ());
		
		cartItemRepo.deleteAll (cart);
		
		userRepo.save (user);
	}
}
