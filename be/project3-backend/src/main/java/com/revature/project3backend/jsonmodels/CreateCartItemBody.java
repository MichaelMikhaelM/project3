package com.revature.project3backend.jsonmodels;

import lombok.Data;

/**
 * CreateCartItemBody is used as the body for the method in CartItemController that creates a CartItem
 */
@Data
public class CreateCartItemBody {
	/**
	 * The id of the product that the cart item refers to
	 */
	private Integer productId;
	
	/**
	 * The quantity of the product that the cart item refers to
	 */
	private Integer quantity;
}
