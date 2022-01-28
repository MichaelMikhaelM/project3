package com.revature.project3backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.exceptions.UnauthorizedException;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.models.*;
import com.revature.project3backend.services.ProductService;
import com.revature.project3backend.services.TransactionService;
import com.revature.project3backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest (TransactionController.class)
class TransactionControllerIT {
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	TransactionService transactionService;
	
	@MockBean
	UserService userService;
	
	@MockBean
	ProductService productService;
	
	private final ObjectMapper json = new ObjectMapper ();
	
	@Test
	void createTransaction () throws Exception {
		User user = new User (1, "first", "last", "email", "username", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER"));
		
		MockHttpSession mockHttpSession = new MockHttpSession ();
		mockHttpSession.setAttribute ("user", user);
		
		Product product = new Product (1, "roomba", "description", 10f, "1.jpg", null, 10);
		Product product0 = new Product (12, "roomba", "description", 10f, "2.jpg", null, 10);
		
		List <CartItem> items = new ArrayList <> ();
		
		items.add (new CartItem (new User (), product, 1));
		items.add (new CartItem (new User (), product0, 1));
		
		Transaction transaction = new Transaction (1, user, "", 20.00f);
		
		user.setCart (items);
		
		Mockito.when (transactionService.createTransaction (Mockito.any (Transaction.class), Mockito.eq (items))).thenReturn (transaction);
		
		mvc.perform (MockMvcRequestBuilders.post ("/transaction")
			.contentType (MediaType.APPLICATION_JSON)
			.session (mockHttpSession))
			
			.andExpect (MockMvcResultMatchers.status ().isOk ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse ("Created transaction", true, transaction))));
		
		Mockito.verify (productService).reduceStock (product, items.get (0).getQuantity ());
		Mockito.verify (productService).reduceStock (product0, items.get (1).getQuantity ());
		
		Mockito.verify (transactionService).createTransaction (Mockito.any (Transaction.class), Mockito.eq (items));
		Mockito.verify (userService).clearCart (user);
	}
	
	@Test
	void createTransactionWhenNotLoggedIn () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/transaction")
			.contentType (MediaType.APPLICATION_JSON))
			
			.andExpect (MockMvcResultMatchers.status ().isUnauthorized ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse ("Error! Unauthorized", false, null, "/login"))));
		
		Mockito.verify (transactionService, Mockito.never ()).createTransaction (Mockito.any (), Mockito.any ());
		Mockito.verify (userService, Mockito.never ()).clearCart (Mockito.any ());
	}
	
	@Test
	void createTransactionWhenCartIsEmpty () throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession ();
		mockHttpSession.setAttribute ("user", new User (1, "first", "last", "email", "username", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER")));
		
		mvc.perform (MockMvcRequestBuilders.post ("/transaction")
			.contentType (MediaType.APPLICATION_JSON)
			.session (mockHttpSession))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse ("Error! Invalid cart", false, null, null))));
		
		Mockito.verify (productService, Mockito.never ()).reduceStock (Mockito.any (), Mockito.any ());
		
		Mockito.verify (transactionService, Mockito.never ()).createTransaction (Mockito.any (), Mockito.any ());
		Mockito.verify (userService, Mockito.never ()).clearCart (Mockito.any ());
	}
	
	@Test
	void getTransaction () throws Exception {
		User user = new User (1, "first", "last", "email", "username", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER"));
		
		MockHttpSession mockHttpSession = new MockHttpSession ();
		mockHttpSession.setAttribute ("user", user);
		
		User fakeUser = new User ();
		fakeUser.setId (1);
		
		Transaction transaction = new Transaction (1, fakeUser, "", 20.00f);
		
		user.getTransactions ().add (transaction);
		
		Mockito.when (transactionService.getTransaction (transaction.getId ())).thenReturn (transaction);
		
		mvc.perform (MockMvcRequestBuilders.get ("/transaction/" + user.getId ())
			.contentType (MediaType.APPLICATION_JSON)
			.session (mockHttpSession))
			
			.andExpect (MockMvcResultMatchers.status ().isOk ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse ("Got transaction", true, transaction))));
		
		Mockito.verify (transactionService).getTransaction (transaction.getId ());
	}
	
	@Test
	void getTransactionWhenNotLoggedIn () throws Exception {
		mvc.perform (MockMvcRequestBuilders.get ("/transaction/1")
			.contentType (MediaType.APPLICATION_JSON))
			
			.andExpect (MockMvcResultMatchers.status ().isUnauthorized ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new UnauthorizedException (), "/login"))));
		
		Mockito.verify (transactionService, Mockito.never ()).getTransaction (Mockito.any ());
	}
	
	@Test
	void getTransactionWhenTransactionWasMadeByOtherUser () throws Exception {
		int transactionId = 1;
		
		User user1 = new User (1, "first", "last", "email", "username", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER"));
		User user2 = new User (2, "second", "last", "email2", "username2", "password", new ArrayList <> (), new ArrayList <> (), new UserRole (2, "USER"));
		
		MockHttpSession mockHttpSession = new MockHttpSession ();
		mockHttpSession.setAttribute ("user", user2);
		
		Transaction transaction = new Transaction (transactionId, user1, "", 20.00f);
		
		user1.getTransactions ().add (transaction);
		
		Mockito.when (transactionService.getTransaction (transaction.getId ())).thenReturn (transaction);
		
		mvc.perform (MockMvcRequestBuilders.get ("/transaction/" + transactionId)
			.contentType (MediaType.APPLICATION_JSON)
			.session (mockHttpSession))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid transaction id")))));
		
		Mockito.verify (transactionService).getTransaction (transactionId);
	}
}