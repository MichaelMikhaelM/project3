package com.revature.project3backend.jsonmodels;

import lombok.Data;

/**
 * UpdateCartItemBody is used as the body for the method in CartItemController that updates the quantity of a cart item
 */
@Data
public class UpdateCartItemBody {
	/**
	 * The quantity to set the cart item's quantity to
	 */
	private Integer quantity;
}
