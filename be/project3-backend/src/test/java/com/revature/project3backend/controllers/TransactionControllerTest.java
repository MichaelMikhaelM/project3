package com.revature.project3backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.exceptions.UnauthorizedException;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.models.CartItem;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.models.Transaction;
import com.revature.project3backend.models.User;
import com.revature.project3backend.services.ProductService;
import com.revature.project3backend.services.TransactionService;
import com.revature.project3backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionControllerTest {
	private final TransactionController transactionController;
	
	private final TransactionService transactionService = Mockito.mock (TransactionService.class);
	private final UserService userService = Mockito.mock (UserService.class);
	private final ProductService productService = Mockito.mock (ProductService.class);
	
	public TransactionControllerTest () {
		this.transactionController = new TransactionController (this.transactionService, this.userService, this.productService);
	}
	
	@Test
	void createTransaction () throws JsonProcessingException, InvalidValueException, UnauthorizedException {
		int stock = 10;
		
		User user = new User ("first", "last", "email", "username", "password");
		
		Product product = new Product (1, "roomba", "description", 10f, "1.jpg", null, stock);
		Product product0 = new Product (12, "roomba", "description", 10f, "2.jpg", null, stock);
		
		List <CartItem> items = new ArrayList <> ();
		
		items.add (new CartItem (new User (), product, 1));
		items.add (new CartItem (new User (), product0, 5));
		
		user.setCart (items);
		
		Transaction transaction = new Transaction (user);
		Transaction expectedCreate = new Transaction (1, user, items.toString (), 20.0f);
		
		MockHttpSession mockHttpSession = new MockHttpSession ();
		mockHttpSession.setAttribute ("user", user);
		
		Mockito.when (transactionService.createTransaction (transaction, items)).thenReturn (expectedCreate);
		
		assertEquals (ResponseEntity.ok (new JsonResponse ("Created transaction", true, expectedCreate)), transactionController.createTransaction (mockHttpSession));
		
		Mockito.verify (productService).reduceStock (product, items.get (0).getQuantity ());
		Mockito.verify (productService).reduceStock (product0, items.get (1).getQuantity ());
		
		Mockito.verify (transactionService).createTransaction (transaction, items);
		Mockito.verify (userService).clearCart (user);
	}
	
	@Test
	void createTransactionWhenNotLoggedIn () throws JsonProcessingException, InvalidValueException, UnauthorizedException {
		MockHttpSession mockHttpSession = new MockHttpSession ();
		
		UnauthorizedException exception = assertThrows (UnauthorizedException.class, () -> this.transactionController.createTransaction (mockHttpSession));
		
		assertEquals ("Error! Unauthorized", exception.getMessage ());
		
		Mockito.verify (productService, Mockito.never ()).reduceStock (Mockito.any (), Mockito.any ());
		
		Mockito.verify (transactionService, Mockito.never ()).createTransaction (Mockito.any (), Mockito.any ());
		Mockito.verify (userService, Mockito.never ()).clearCart (Mockito.any ());
	}
	
	@Test
	void createTransactionWhenCartIsEmpty () throws JsonProcessingException, InvalidValueException, UnauthorizedException {
		User user = new User ("first", "last", "email", "username", "password");
		
		MockHttpSession mockHttpSession = new MockHttpSession ();
		mockHttpSession.setAttribute ("user", user);
		
		user.setCart (new ArrayList <> ());
		
		InvalidValueException exception = assertThrows (InvalidValueException.class, () -> this.transactionController.createTransaction (mockHttpSession));
		
		assertEquals ("Error! Invalid cart", exception.getMessage ());
		
		Mockito.verify (productService, Mockito.never ()).reduceStock (Mockito.any (), Mockito.any ());
		
		Mockito.verify (transactionService, Mockito.never ()).createTransaction (Mockito.any (), Mockito.any ());
		Mockito.verify (userService, Mockito.never ()).clearCart (Mockito.any ());
	}
	
	@Test
	void getTransaction () throws JsonProcessingException, InvalidValueException, UnauthorizedException {
		User user = new User ("first", "last", "email", "username", "password");
		User user0 = new User ("first", "last", "email", "username", "password");
		
		user.setId (1);
		
		user0.setCart (null);
		
		Product product = new Product (1, "roomba", "description", 10f, "1.jpg", null, 10);
		Product product0 = new Product (12, "roomba", "description", 10f, "2.jpg", null, 10);
		
		List <CartItem> items = new ArrayList <> ();
		
		items.add (new CartItem (user0, product, 1));
		items.add (new CartItem (user0, product0, 1));
		
		user.setCart (items);
		
		Transaction transaction = new Transaction (user);
		
		MockHttpSession mockHttpSession = new MockHttpSession ();
		mockHttpSession.setAttribute ("user", user);
		
		Mockito.when (transactionService.getTransaction (transaction.getId ())).thenReturn (transaction);
		
		ResponseEntity <JsonResponse> expected = ResponseEntity.ok (new JsonResponse ("Got transaction", true, transaction));
		
		assertEquals (expected, transactionController.getTransaction (transaction.getId (), mockHttpSession));
		
		Mockito.verify (transactionService).getTransaction (transaction.getId ());
	}
	
	@Test
	void getTransactionWhenNotLoggedIn () throws InvalidValueException, UnauthorizedException, JsonProcessingException {
		MockHttpSession mockHttpSession = new MockHttpSession ();
		
		UnauthorizedException exception = assertThrows (UnauthorizedException.class, () -> this.transactionController.getTransaction (1, mockHttpSession));
		
		assertEquals ("Error! Unauthorized", exception.getMessage ());
		
		Mockito.verify (transactionService, Mockito.never ()).getTransaction (Mockito.any ());
	}
	
	@Test
	void getTransactionWhenTransactionWasMadeByOtherUser () throws InvalidValueException, UnauthorizedException {
		int transactionId = 1;
		
		User user = new User ("first", "last", "email", "username", "password");
		User user0 = new User ("first0", "last0", "email0", "username0", "password0");
		
		user.setId (1);
		user0.setId (2);
		
		MockHttpSession mockHttpSession = new MockHttpSession ();
		mockHttpSession.setAttribute ("user", user0);
		
		Mockito.when (transactionService.getTransaction (transactionId)).thenReturn (new Transaction (user));
		
		InvalidValueException exception = assertThrows (InvalidValueException.class, () -> this.transactionController.getTransaction (transactionId, mockHttpSession));
		
		assertEquals ("Error! Invalid transaction id", exception.getMessage ());
		
		Mockito.verify (transactionService).getTransaction (transactionId);
	}
}