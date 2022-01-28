package com.revature.project3backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.models.*;
import com.revature.project3backend.repositories.TransactionRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionServiceTest {
	TransactionService transactionService;
	TransactionRepo transactionRepo = Mockito.mock (TransactionRepo.class);
	
	public TransactionServiceTest () {
		this.transactionService = new TransactionService (transactionRepo);
	}
	
	@Test
	void createTransaction () throws JsonProcessingException {
		List <CartItem> items = new ArrayList <> ();
		
		Product product = new Product (1, "something", "something", 3000.00f, "image", null, 1);
		
		User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", "pass123", items, null, new UserRole (2, "USER"));
		
		CartItem cartItem = new CartItem (1, user, product, 1);
		
		items.add (cartItem);
		
		Transaction expectedResult = new Transaction (1, user, "[]", 300.00f);
		
		Mockito.when (transactionRepo.save (expectedResult)).thenReturn (expectedResult);
		
		assertEquals (expectedResult, transactionService.createTransaction (expectedResult, items));
		
		Mockito.verify (transactionRepo).save (expectedResult);
	}
	
	@Test
	void getTransaction () throws InvalidValueException {
		List <CartItem> items = new ArrayList <> ();
				
		Product product = new Product (1, "something", "something", 3000.00f, "image", null, 1);
		
		User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", "pass123", items, null, new UserRole (2, "USER"));
		
		CartItem cartItem = new CartItem (1, user, product, 1);
		
		items.add (cartItem);
		
		Transaction expectedResult = new Transaction (1, user, "items", 300.00f);
		
		Mockito.when (transactionRepo.findById (expectedResult.getId ())).thenReturn (Optional.of (expectedResult));
		
		assertEquals (expectedResult, transactionService.getTransaction (expectedResult.getId ()));
		
		Mockito.verify (transactionRepo).findById (expectedResult.getId ());
	}
	
	@Test
	void getTransactionWhenNotFound () {
		int id = 1;
		
		Mockito.when (transactionRepo.findById (id)).thenReturn (Optional.empty ());
		
		InvalidValueException exception = assertThrows (InvalidValueException.class, () -> transactionService.getTransaction (id));
		
		Mockito.verify (transactionRepo).findById (id);
		
		assertEquals ("Error! Invalid transaction id", exception.getMessage ());
	}
}