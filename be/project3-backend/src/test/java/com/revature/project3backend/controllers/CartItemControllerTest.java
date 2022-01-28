package com.revature.project3backend.controllers;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.exceptions.UnauthorizedException;
import com.revature.project3backend.jsonmodels.CreateCartItemBody;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.jsonmodels.UpdateCartItemBody;
import com.revature.project3backend.models.CartItem;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.models.User;
import com.revature.project3backend.models.UserRole;
import com.revature.project3backend.repositories.CartItemRepo;
import com.revature.project3backend.repositories.ProductRepo;
import com.revature.project3backend.repositories.UserRepo;
import com.revature.project3backend.services.CartItemService;
import com.revature.project3backend.services.ProductService;
import com.revature.project3backend.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class  CartItemControllerTest {
    private final CartItemRepo cartItemRepo = Mockito.mock(CartItemRepo.class);
    private final ProductRepo productRepo = Mockito.mock(ProductRepo.class);
    private final UserRepo userRepo = Mockito.mock(UserRepo.class);

    private final CartItemService cartItemService;
    private final ProductService productService;
    private final UserService userService;

    private CartItemController cartItemController;

    private final List<Product> products = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    public CartItemControllerTest() {
        cartItemService = new CartItemService(cartItemRepo);
        productService = new ProductService(productRepo);
        userService = new UserService(userRepo, cartItemRepo);
        cartItemController = new CartItemController(cartItemService, productService, userService);
    }

    @BeforeEach
    void setUp() {
        products.add (new Product(1, "Java I", "A beginner Java course", 10.00F, "", 8.00F, 5));
        products.add (new Product (2, "Java II", "An intermediate Java course", 20.00F, "", 18.00F, 5));
        products.add (new Product (3, "Python I", "A beginner Python course", 10.00F, "", 8.00F, 5));
        products.add (new Product (4, "Python II", "An intermediate Python course", 20.00F, "", 18.00F, 5));
        users.add (new User (1, "User", "1", "email1", "username13123", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER")));
        users.add (new User (2, "User", "2", "email2", "username22425", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER")));
        users.add (new User (3, "User", "3", "email3", "username32323", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER")));
    }

    @AfterEach
    void tearDown() {
        users.clear();
        products.clear();
    }

	@Test
	void createCartItem() throws InvalidValueException, UnauthorizedException {
		CreateCartItemBody body = new CreateCartItemBody();

		body.setProductId(2);    // Java II
		body.setQuantity(2);

		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(1)); // User 2

		Mockito.when(productRepo.findById(2)).thenReturn(java.util.Optional.of(products.get(1)));   // Java II

		List<CartItem> newCart = new ArrayList<>();

		CartItem cartItem = new CartItem(users.get(1), products.get(1), 2);

		newCart.add(cartItem);

		assertEquals(ResponseEntity.ok(new JsonResponse("Added to cart", true)), cartItemController.createCartItem(body, httpSession));
		assertEquals(newCart, users.get(1).getCart());

		Mockito.verify(cartItemRepo).save(cartItem);
		Mockito.verify(userRepo).save(users.get(1));
	}

	@Test
	void createCartItemWhenNotLoggedIn() {
		CreateCartItemBody body = new CreateCartItemBody();

		body.setProductId(2);    // Java II
		body.setQuantity(2);

		MockHttpSession httpSession = new MockHttpSession();

		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> cartItemController.createCartItem(body, httpSession));

		assertEquals("Error! Unauthorized", exception.getMessage());

		Mockito.verify(productRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
		Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void createCartItemWhenProductIdIsNull() {
		CreateCartItemBody body = new CreateCartItemBody();

		//we are not setting the productId, so it's null
		body.setQuantity(2);

		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(2));

		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.createCartItem(body, httpSession));

		assertEquals("Error! Invalid product id", exception.getMessage());

		Mockito.verify(productRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
		Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void createCartItemWhenQuantityIsNull() {
		CreateCartItemBody body = new CreateCartItemBody();

		body.setProductId(2); // Java II
		//we are not setting the quantity, so it's null

		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(0));

		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.createCartItem(body, httpSession));

		assertEquals("Error! Invalid quantity", exception.getMessage());

		Mockito.verify(productRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
		Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void createCartItemWhenQuantityIsLessThanOne() {
		CreateCartItemBody body = new CreateCartItemBody();

		body.setProductId(2); // Java II
		body.setQuantity(0);  // Set quantity to invalid value e.g. < 1

		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(0));

		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.createCartItem(body, httpSession));

		assertEquals("Error! Invalid quantity", exception.getMessage());

		Mockito.verify(productRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
		Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void createCartItemWhenItemIsAlreadyInCart() {
		CreateCartItemBody body = new CreateCartItemBody();

		body.setProductId(2);    // Java II
		body.setQuantity(5);

		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(1)); // User 2

		// Pre-populate user 2's cart with existing cart items which already contains Java II course
		List<CartItem> cartItems = new ArrayList<>();

		cartItems.add(new CartItem(users.get(1), products.get(1), 2)); // pre-populate with existing item Java II

		users.get(1).setCart(cartItems);    // Set user 2's cart to contain existing items (just the Java II course)

		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.createCartItem(body, httpSession));

		assertEquals("Error! Invalid product id", exception.getMessage());

		Mockito.verify(productRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
		Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void createCartItemWhenQuantityIsHigherThanStock() {
		CreateCartItemBody body = new CreateCartItemBody();

		body.setProductId(4);        // Python II
		body.setQuantity(6);        // Existing stock of Python II course is 5 on purpose exceed it for test

		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(1)); // User 2

		// Pre-populate user 2's cart with existing cart items but NOT the Java II course
		List<CartItem> cartItems = new ArrayList<>();

		cartItems.add(new CartItem(users.get(1), products.get(1), 2)); // pre-populate with existing item Java II

		users.get(1).setCart(cartItems);

		Mockito.when(productRepo.findById(4)).thenReturn(java.util.Optional.of(products.get(3)));   // Python II

		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.createCartItem(body, httpSession));

		assertEquals("Error! Invalid quantity", exception.getMessage());

		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
		Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void getCartItems() throws UnauthorizedException {
		List<CartItem> cart = new ArrayList<>();

		cart.add(new CartItem(users.get(2), products.get(1), 2)); // pre-populate with existing item Java II
		cart.add(new CartItem(users.get(2), products.get(2), 3)); // pre-populate with existing item Python I

		users.get(2).setCart(cart);    // User 3

		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(2)); // User 3

		ResponseEntity<JsonResponse> getCartItemsResponse = cartItemController.getCartItems(httpSession);

		assertEquals(ResponseEntity.ok(new JsonResponse("Got " + cart.size() + " cart items", true, cart)), getCartItemsResponse);
		assertEquals(cart, getCartItemsResponse.getBody().getData());
	}

	@Test
	void getCartItemsWhenNotLoggedIn() {
		MockHttpSession httpSession = new MockHttpSession();

		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> cartItemController.getCartItems(httpSession));

		assertEquals("Error! Unauthorized", exception.getMessage());
	}

	@Test
	void updateCartItem() throws InvalidValueException, UnauthorizedException {
		int cartItemId = 2;
		int quantity = 4;

		UpdateCartItemBody body = new UpdateCartItemBody();
		body.setQuantity(quantity);

		// pre-populate cart for user 1
		List<CartItem> cart = new ArrayList<>();
		cart.add(new CartItem(1, users.get(0), products.get(1), 2)); // pre-populate with existing item Java II
		cart.add(new CartItem(2, users.get(0), products.get(2), 3)); // pre-populate with existing item Python I
		users.get(0).setCart(cart);    // User 1

		MockHttpSession httpSession = new MockHttpSession();
		httpSession.setAttribute("user", users.get(0)); // User 1

		Mockito.when(cartItemRepo.findById(cartItemId)).thenReturn(java.util.Optional.of(cart.get(1)));   // Python I in user's cart

		assertEquals(ResponseEntity.ok(new JsonResponse("Updated cart item quantity", true)), cartItemController.updateCartItem(2, body, httpSession));

		assertEquals(quantity, users.get(0).getCart().get(1).getQuantity());

		Mockito.verify(cartItemRepo).save(cart.get(1));
	}

	@Test
	void updateCartItemWhenNotLoggedIn() {
		UpdateCartItemBody body = new UpdateCartItemBody();

		MockHttpSession httpSession = new MockHttpSession();

		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> cartItemController.updateCartItem(1, body, httpSession));

		assertEquals("Error! Unauthorized", exception.getMessage());

		Mockito.verify(cartItemRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void updateCartItemWhenQuantityIsNull() {
		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(0));

		UpdateCartItemBody body = new UpdateCartItemBody();

		//we are not setting the quantity, so it's null

		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.updateCartItem(1, body, httpSession));

		assertEquals("Error! Invalid quantity", exception.getMessage());

		Mockito.verify(cartItemRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void updateCartItemWhenQuantityIsHigherThanStock() {
		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(0)); // User 1

		UpdateCartItemBody body = new UpdateCartItemBody();

		body.setQuantity(6);    // Stock of cart item id=2, product Python I

		// pre-populate cart for user 1
		List<CartItem> cart = new ArrayList<>();
		cart.add(new CartItem(1, users.get(0), products.get(1), 2)); // pre-populate with existing item Java II
		cart.add(new CartItem(2, users.get(0), products.get(2), 3)); // pre-populate with existing item Python I
		users.get(0).setCart(cart);    // User 1

		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.updateCartItem(2, body, httpSession));
		assertEquals("Error! Invalid quantity", exception.getMessage());
		Mockito.verify(cartItemRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void updateCartItemWhenItemIsNotInCart() {
		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(0)); // User 1

		UpdateCartItemBody body = new UpdateCartItemBody();
		body.setQuantity(4);

		// pre-populate cart for user 1
		List<CartItem> cart = new ArrayList<>();

		cart.add(new CartItem(1, users.get(0), products.get(1), 2)); // pre-populate with existing item Java II
		cart.add(new CartItem(2, users.get(0), products.get(2), 3)); // pre-populate with existing item Python I

		users.get(0).setCart(cart);    // User 1

		// cart item id 3 does not exist in the cart
		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.updateCartItem(3, body, httpSession));

		assertEquals("Error! Invalid cart item id", exception.getMessage());

		Mockito.verify(cartItemRepo, Mockito.never()).findById(Mockito.any());
		Mockito.verify(cartItemRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void deleteCartItem() throws InvalidValueException, UnauthorizedException {
		MockHttpSession httpSession = new MockHttpSession();

		httpSession.setAttribute("user", users.get(0)); // User 1

		// pre-populate cart for user 1
		List<CartItem> cart = new ArrayList<>();

		cart.add(new CartItem(1, users.get(0), products.get(1), 2)); // pre-populate with existing item Java II
		cart.add(new CartItem(2, users.get(0), products.get(2), 3)); // pre-populate with existing item Python I

		users.get(0).setCart(cart);    // User 1

		CartItem cartItemToDelete = new CartItem(2, users.get(0), products.get(2), 3); // clone of second cart item Python I

		assertEquals(ResponseEntity.ok(new JsonResponse("Deleted cart item", true)), cartItemController.deleteCartItem(2, httpSession));

		List<CartItem> expectedResult = new ArrayList<>();

		expectedResult.add(new CartItem(1, users.get(0), products.get(1), 2));

		assertEquals(expectedResult, users.get(0).getCart());

		Mockito.verify(cartItemRepo).delete(cartItemToDelete);    // Verify Python I course had .delete() method called on it
		Mockito.verify(userRepo).save(users.get(0));
	}

	@Test
	void deleteCartItemWhenNotLoggedIn() {
		MockHttpSession httpSession = new MockHttpSession();

		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> cartItemController.deleteCartItem(1, httpSession));

		assertEquals("Error! Unauthorized", exception.getMessage());

		Mockito.verify(cartItemRepo, Mockito.never()).delete(Mockito.any());    // Verify Python I course had .delete() method called on it
		Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
	}

	@Test
	void deleteCartItemWhenItemIsNotInCart() {
		MockHttpSession httpSession = new MockHttpSession();
		httpSession.setAttribute("user", users.get(2)); // User 1

		// pre-populate cart for user 3
		List<CartItem> cart = new ArrayList<>();

		cart.add(new CartItem(1, users.get(2), products.get(1), 2)); // pre-populate with existing item Java II
		cart.add(new CartItem(2, users.get(2), products.get(2), 3)); // pre-populate with existing item Python I

		users.get(2).setCart(cart);    // User 1

		// cart item with id 3 does not exist
		InvalidValueException exception = assertThrows(InvalidValueException.class, () -> cartItemController.deleteCartItem(3, httpSession));

		assertEquals("Error! Invalid cart item id", exception.getMessage());

		Mockito.verify(cartItemRepo, Mockito.never()).delete(Mockito.any());    // Verify Python I course had .delete() method called on it
		Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
	}
}
