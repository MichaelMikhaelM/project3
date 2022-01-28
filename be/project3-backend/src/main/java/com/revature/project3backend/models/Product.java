package com.revature.project3backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Products are things that the user can buy
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
public class Product {
	/**
	 * The id of the product
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
	 * The name of the product
	 */
	@Column (nullable = false)
	private String name;
	
	/**
	 * The description of the product
	 */
	@Column (nullable = false)
	private String description;
	
	/**
	 * The price of the product (if the product is on sale, this is the non-sale price)
	 */
	@Column (nullable = false)
	private Float price;
	
	/**
	 * The url of the image of the product
	 */
	private String imageUrl;
	
	/**
	 * The sale price of the product (if this is null, the product is not on sale)
	 */
	private Float salePrice;
	
	/**
	 * The stock of the product
	 */
	private Integer stock;
	
	/**
	 * This constructor is used to create a product with only the needed properties
	 *
	 * @param id The id of the product
	 * @param name The name of the product
	 * @param description The description of the product
	 * @param price The price of the product (if the product is on sale, this is the non-sale price)
	 * @param imageUrl The url of the image of the product
	 * @param stock The stock of the product
	 */
	public Product (Integer id, String name, String description, Float price, String imageUrl, Integer stock) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageUrl = imageUrl;
		this.stock = stock;
	}
}
