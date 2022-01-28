package com.revature.project3backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Transactions are completed orders made by the user
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
public class Transaction {
	/**
	 * The id of the transaction
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
	 * The user that completed the transaction
	 */
	@ManyToOne (optional = false)
	@JsonProperty (access = JsonProperty.Access.WRITE_ONLY)
	private User buyer;
	
	/**
	 * The items in the transaction (formatted as a JSON string)
	 */
	@Column (length = 1000)
	private String items;
	
	/**
	 * The total price of the transaction
	 */
	@Column (nullable = false)
	private Float total;
	
	/**
	 * This constructor is used to create a transaction with only the needed properties
	 *
	 * @param buyer The user that completed the transaction
	 */
	public Transaction (User buyer) {
		this.buyer = buyer;
	}
}
