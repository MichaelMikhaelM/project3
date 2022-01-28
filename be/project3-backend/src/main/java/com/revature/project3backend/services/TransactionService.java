package com.revature.project3backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.models.CartItem;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.models.Transaction;
import com.revature.project3backend.repositories.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * TransactionService contains the Service layer methods for transactions
 */
@Service
@Transactional
public class TransactionService {
	/**
	 * The instance of TransactionRepo to use
	 */
	private final TransactionRepo transactionRepo;
	
	/**
	 * The instance of ObjectMapper to use
	 */
	private final ObjectMapper json = new ObjectMapper ();
	
	/**
	 * This constructor is automatically called by Spring
	 *
	 * @param transactionRepo The instance of TransactionRepo to use
	 */
	@Autowired
	public TransactionService (TransactionRepo transactionRepo) {
		this.transactionRepo = transactionRepo;
	}
	
	/**
	 * Create a transaction
	 *
	 * @param transaction The transaction to create
	 * @param items The cart items used in the transaction
	 * @return The created Transaction
	 * @throws JsonProcessingException Thrown when there is an error while parsing JSON
	 */
	public Transaction createTransaction (Transaction transaction, List <CartItem> items) throws JsonProcessingException {
		float total = 0f;
		
		for (CartItem item : items) {
			Product product = item.getProduct ();
			
			total += (product.getSalePrice () == null ? product.getPrice () : product.getSalePrice ()) * item.getQuantity ();
		}
		
		transaction.setTotal (total);
		transaction.setItems (json.writeValueAsString (items));
		
		return transactionRepo.save (transaction);
	}
	
	/**
	 * Gets a transaction given its id
	 *
	 * @param transactionId The id to use to get the transaction
	 * @return The found transaction
	 * @throws InvalidValueException Thrown when validation fails
	 */
	public Transaction getTransaction (Integer transactionId) throws InvalidValueException {
		Transaction transaction = transactionRepo.findById (transactionId).orElse (null);
		
		if (transaction == null) {
			throw new InvalidValueException ("Invalid transaction id");
		}
		
		return transaction;
	}
}
