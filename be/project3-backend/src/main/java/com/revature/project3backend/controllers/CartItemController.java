package com.revature.project3backend.controllers;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.exceptions.UnauthorizedException;
import com.revature.project3backend.jsonmodels.CreateCartItemBody;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.jsonmodels.UpdateCartItemBody;
import com.revature.project3backend.models.CartItem;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.models.User;
import com.revature.project3backend.services.CartItemService;
import com.revature.project3backend.services.ProductService;
import com.revature.project3backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * CartItemController handles requests concerning cart items
 */
@RestController
@RequestMapping ("cartitem")
@CrossOrigin (origins = "http://localhost:4200/", allowCredentials = "true")
public class CartItemController {
	/**
	 * The instance of CartItemService to use
	 */
	private final CartItemService cartItemService;
	
	/**
	 * The instance of ProductService to use
	 */
	private final ProductService productService;
	
	/**
	 * The instance of UserService to use
	 */
	private final UserService userService;
	
	/**
	 * This constructor is automatically called by Spring
	 *
	 * @param cartItemService The instance of CartItemService to use
	 * @param productService The instance of ProductService to use
	 * @param userService The instance of UserService to use
	 */
	@Autowired
	public CartItemController (CartItemService cartItemService, ProductService productService, UserService userService) {
		this.cartItemService = cartItemService;
		this.productService = productService;
		this.userService = userService;
	}
	
	/**
	 * Creates a cart item and then adds it to the user's cart
	 *
	 * @param body The data to use to create the cart item, contains a product id and a quantity
	 * @param httpSession The HTTP session of the user
	 * @return A ResponseEntity used to create the HTTP response, contains the user's cart
	 * @throws InvalidValueException Thrown when validation fails
	 * @throws UnauthorizedException Thrown when the user is not logged in
	 */
	@PostMapping
	public ResponseEntity <JsonResponse> createCartItem (@RequestBody CreateCartItemBody body, HttpSession httpSession) throws InvalidValueException, UnauthorizedException {
		User user = (User) httpSession.getAttribute ("user");
		
		if (user == null) {
			throw new UnauthorizedException ();
		}
		
		if (body.getProductId () == null) {
			throw new InvalidValueException ("Invalid product id");
		}
		
		if (body.getQuantity () == null) {
			throw new InvalidValueException ("Invalid quantity");
		}
		
		if (body.getQuantity () < 1) {
			throw new InvalidValueException ("Invalid quantity");
		}
		
		for (CartItem cartItem : user.getCart ()) {
			//if product is already in cart
			if (body.getProductId ().equals (cartItem.getProduct ().getId ())) {
				throw new InvalidValueException ("Invalid product id");
			}
		}
		
		Product product = productService.getProduct (body.getProductId ());
		
		if (product.getStock () - body.getQuantity () < 0) {
			throw new InvalidValueException ("Invalid quantity");
		}
		
		CartItem cartItem = new CartItem (user, product, body.getQuantity ());
		
		cartItemService.createCartItem (cartItem);
		
		userService.addToCart (user, cartItem);
		
		return ResponseEntity.ok (new JsonResponse ("Added to cart", true));
	}
	
	/**
	 * Gets the cart items in the user's cart
	 *
	 * @param httpSession The HTTP session of the user
	 * @return A ResponseEntity used to create the HTTP response, contains the user's cart
	 * @throws UnauthorizedException Thrown when the user is not logged in
	 */
	@GetMapping
	public ResponseEntity <JsonResponse> getCartItems (HttpSession httpSession) throws UnauthorizedException {
		User user = (User) httpSession.getAttribute ("user");
		
		if (user == null) {
			throw new UnauthorizedException ();
		}
		
		List <CartItem> cart = user.getCart ();
		
		return ResponseEntity.ok (new JsonResponse ("Got " + cart.size () + " cart items", true, cart));
	}
	
	/**
	 * Updates the quantity of a cart item in a user's cart
	 *
	 * @param cartItemId The id of the cart item to update
	 * @param body The data to use to update the cart item
	 * @param httpSession The HTTP session of the user
	 * @return A ResponseEntity used to create the HTTP response
	 * @throws InvalidValueException Thrown when validation fails
	 * @throws UnauthorizedException Thrown when the user is not logged in
	 */
	@PutMapping ("{cartItemId}")
	public ResponseEntity <JsonResponse> updateCartItem (@PathVariable Integer cartItemId, @RequestBody UpdateCartItemBody body, HttpSession httpSession) throws InvalidValueException, UnauthorizedException {
		User user = (User) httpSession.getAttribute ("user");
		
		if (user == null) {
			throw new UnauthorizedException ();
		}
		
		if (body.getQuantity () == null) {
			throw new InvalidValueException ("Invalid quantity");
		}
		
		for (int i = 0; i < user.getCart ().size (); i++) {
			if (user.getCart ().get (i).getId ().equals (cartItemId)) {
				if (user.getCart ().get (i).getProduct ().getStock () - body.getQuantity () < 0) {
					throw new InvalidValueException ("Invalid quantity");
				}
				
				cartItemService.updateCartItem (cartItemId, body.getQuantity ());
				
				return ResponseEntity.ok (new JsonResponse ("Updated cart item quantity", true));
			}
		}
		
		throw new InvalidValueException ("Invalid cart item id");
	}
	
	/**
	 * Removes a cart item from a user's cart and deletes it
	 *
	 * @param cartItemId The cart item to remove from the user's cart and delete
	 * @param httpSession The HTTP session of the user
	 * @return A ResponseEntity used to create the HTTP response
	 * @throws InvalidValueException Thrown when validation fails
	 * @throws UnauthorizedException Thrown when the user is not logged in
	 */
	@DeleteMapping ("{cartItemId}")
	public ResponseEntity <JsonResponse> deleteCartItem (@PathVariable Integer cartItemId, HttpSession httpSession) throws InvalidValueException, UnauthorizedException {
		User user = (User) httpSession.getAttribute ("user");
		
		if (user == null) {
			throw new UnauthorizedException ();
		}
		
		for (int i = 0; i < user.getCart ().size (); i++) {
			if (user.getCart ().get (i).getId ().equals (cartItemId)) {
				userService.removeFromCart (user, user.getCart ().get (i));
				
				return ResponseEntity.ok (new JsonResponse ("Deleted cart item", true));
			}
		}
		
		throw new InvalidValueException ("Invalid cart item id");
	}
}
