package com.revature.project3backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Users are people that shop for products (the table name is "users" because "user" is a restricted keyword in PostgreSQL)
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table (name = "users")
public class User {
	/**
	 * The id of the user
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
	 * The first name of the user
	 */
	@Column (nullable = false)
	private String firstName;
	
	/**
	 * The last name of the user
	 */
	@Column (nullable = false)
	private String lastName;
	
	/**
	 * The email address of the user
	 */
	@Column (unique = true, nullable = false)
	private String email;
	
	/**
	 * The username of the user
	 */
	@Column (unique = true, nullable = false)
	private String username;
	
	/**
	 * The password of the user
	 */
	@Column (nullable = false)
	@JsonProperty (access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	/**
	 * The list of cart items in the user's cart
	 */
	@OneToMany (mappedBy = "buyer")
	private List <CartItem> cart;
	
	/**
	 * The list of transactions completed by the user
	 */
	@OneToMany (mappedBy = "buyer")
	private List <Transaction> transactions;
	
	@ManyToOne
//TODO: set default role to USER, also set it in constructor?
	private UserRole role;
	
	/**
	 * This constructor is used to create a cart item with only the needed properties
	 *
	 * @param firstName The first name of the user
	 * @param lastName The last name of the user
	 * @param email The email address of the user
	 * @param username The username of the user
	 * @param password The password of the user
	 */
	public User (String firstName, String lastName, String email, String username, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
		this.password = password;
	}
}
