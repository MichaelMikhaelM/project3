package com.revature.project3backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * CartItems are what makes up the user's cart
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
public class CartItem {
	/**
	 * The id of the cart item
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
	 * The user that is buying the product
	 */
	@ManyToOne (optional = false)
	@JsonProperty (access = JsonProperty.Access.WRITE_ONLY)
	private User buyer;
	
	/**
	 * The product that is being bought
	 */
	@ManyToOne (optional = false)
	private Product product;
	
	/**
	 * The quantity of the product that is being bought
	 */
	@Column (nullable = false)
	private Integer quantity;
	
	/**
	 * This constructor is used to create a cart item with only the needed properties
	 *
	 * @param buyer The user that is buying the product
	 * @param product The product that is being bought
	 * @param quantity The quantity of the product that is being bought
	 */
	public CartItem (User buyer, Product product, Integer quantity) {
		this.buyer = buyer;
		this.product = product;
		this.quantity = quantity;
	}
}
