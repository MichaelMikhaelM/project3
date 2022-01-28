package com.revature.project3backend.controllers;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.exceptions.UnauthorizedException;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.models.User;
import com.revature.project3backend.services.ProductService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * ProductController handles requests concerning products
 */
@RestController
@RequestMapping ("product")
@CrossOrigin (origins = "http://localhost:4200/", allowCredentials = "true")
public class ProductController {
	/**
	 * The instance of ProductService to use
	 */
	private final ProductService productService;
	
	/**
	 * The default image to use when uploading a product
	 */
	private String defaultImageUrl = "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png";
	
	/**
	 * This constructor is automatically called by Spring
	 *
	 * @param productService The instance of ProductService to use
	 */
	@Autowired
	
	public ProductController (ProductService productService) {
		this.productService = productService;
	}
	
	/**
	 * Gets products that match the given searchQuery
	 *
	 * @param searchQuery The query to use to search the products
	 * @param page The page of products to get
	 * @return A ResponseEntity used to create the HTTP response, contains the products found
	 * @throws InvalidValueException Thrown when validation fails
	 */
	@GetMapping
	public ResponseEntity <JsonResponse> getProducts (@RequestParam String searchQuery, @RequestParam Integer page) throws InvalidValueException {
		if (searchQuery == null) {
			throw new InvalidValueException ("Invalid search query");
		}
		
		if (page == null) {
			throw new InvalidValueException ("Invalid page");
		}
		
		List <Product> products = this.productService.getProducts (searchQuery, page);
		
		return ResponseEntity.ok (new JsonResponse ("Got " + products.size () + " products", true, products));
	}
	
	/**
	 * Gets a product with a given id
	 *
	 * @param id The id of the product to get
	 * @return A ResponseEntity used to create the HTTP response, contains the products found
	 * @throws InvalidValueException Thrown when validation fails
	 */
	@GetMapping ("{id}")
	public ResponseEntity <JsonResponse> getProduct (@PathVariable Integer id) throws InvalidValueException {
		Product product = this.productService.getProduct (id);
		
		return ResponseEntity.ok (new JsonResponse ("Got product", true, product));
	}
	
	/**
	 * Updates an existing product with new information, if no file is provided the imageUrl will stay the same.
	 *
	 * @param productName The product's name.
	 * @param productDescription The product's description.
	 * @param price The product's price.
	 * @param salePrice The product's sale price, null means it's not on sale.
	 * @param id The product's id.
	 * @param file The file for the product's image.
	 * @param stock The amount of stock for that product.
	 * @param imageUrl The existing string for the product's image.
	 * @return It returns a response containing the updated product.
	 */
	@PatchMapping
	public ResponseEntity <JsonResponse> updateProduct (@RequestParam ("name") String productName,
														@RequestParam ("description") String productDescription,
														@RequestParam ("price") Float price,
														@RequestParam (value = "salePrice", required = false) Float salePrice,
														@RequestParam (value = "id") Integer id,
														@RequestParam (value = "file", required = false) MultipartFile file,
														@RequestParam (value = "stock", required = false) Integer stock,
														@RequestParam (value = "imageUrl", required = false) String imageUrl,
														HttpSession httpSession) throws InvalidValueException, UnauthorizedException {
		User user = (User) httpSession.getAttribute ("user");
		System.out.println(user);
		
		if (user == null) {
			throw new UnauthorizedException ();
		}
		
		if (!user.getRole ().getRole ().equals ("ADMIN")) {
			throw new UnauthorizedException ();
		}
		
		Product product = new Product (id, productName, productDescription, price, imageUrl, stock);
		
		if (salePrice != null) {
			product.setSalePrice (salePrice);
			if (product.getSalePrice () < 0) {
				product.setSalePrice (null);
			}
			
			//Error thrown if the sale price is higher than the normal price.
			if (product.getPrice () < product.getSalePrice ()) {
				throw new InvalidValueException ("Sale price cannot be higher than normal price.");
			}
		}
		
		//Error thrown if the price is negative.
		if (product.getPrice () < 0) {
			throw new InvalidValueException ("Price cannot be negative.");
		}
		
		product = this.productService.updateProduct (product, file);
		
		return ResponseEntity.ok (new JsonResponse ("Product updated ok.", true, product, "/product/" + product.getId()));
	}
	
	/**
	 * Takes form data from frontend and uses it create new product
	 *
	 * @param productName name of product
	 * @param productDescription description of product
	 * @param price price of product
	 * @param salePrice sale price of product
	 * @param file multipart image file
	 * @param stock number of items left
	 * @param imageUrl link to image in AWS S3 bucket
	 * @return ResponseEntity with message and status code, returns status code 400 when business logic fails in service layer
	 * @throws InvalidValueException when business logic fails in service layer
	 */
	@PostMapping
	public ResponseEntity <JsonResponse> createProduct (@RequestParam ("name") String productName,
														@RequestParam ("description") String productDescription,
														@RequestParam ("price") Float price,
														@RequestParam (value = "salePrice", required = false) Float salePrice,
														@RequestParam (value = "file", required = false) MultipartFile file,
														@RequestParam (value = "stock", required = false) Integer stock,
														@RequestParam (value = "imageUrl", required = false) String imageUrl,
														HttpSession httpSession) throws InvalidValueException, UnauthorizedException {
		User user = (User) httpSession.getAttribute ("user");
		
		if (user == null) {
			throw new UnauthorizedException ();
		}
		
		if (!user.getRole ().getRole ().equals ("ADMIN")) {
			throw new UnauthorizedException ();
		}
		
		Product product = new Product (null, productName, productDescription, price, imageUrl, stock);

		if (salePrice != null) {
			product.setSalePrice (salePrice);
			if (product.getSalePrice () < 0) {
				product.setSalePrice (null);
			}

			//Error thrown if the sale price is higher than the normal price.
			if (product.getPrice () < product.getSalePrice ()) {
				throw new InvalidValueException ("Sale price cannot be higher than normal price.");
			}
		}

		//Error thrown if the price is negative.
		if (product.getPrice () < 0) {
			throw new InvalidValueException ("Price cannot be negative.");
		}

		// sets default image if none given
		if (file == null) {
			product.setImageUrl (defaultImageUrl);
		}
		
		Product result = this.productService.createProduct (product, file);
		
		return ResponseEntity.ok (new JsonResponse ("Got product updated ok.", true, result, "/product/" + result.getId()));
	}
}
