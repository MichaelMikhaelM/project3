package com.revature.project3backend.repositories;

import com.revature.project3backend.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductRepo is the repository for products
 */
public interface ProductRepo extends JpaRepository <Product, Integer> {
	/**
	 * Gets a page of products based on a name and description. Case is ignored and products must contain the passed name or description
	 *
	 * @param name The string that the product's name must contain to be included in the result (if not found by description)
	 * @param description The string that the product's description must contain to be included in the result (if not found by name)
	 * @param pageable The Pageable object to use to get the page of products
	 * @return Returns a Page containing the products that were found
	 */
	Page <Product> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContaining (String name, String description, Pageable pageable);
}
