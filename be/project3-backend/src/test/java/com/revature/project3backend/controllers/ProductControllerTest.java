package com.revature.project3backend.controllers;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductControllerTest {
	private final ProductController productController;
	private final ProductService productService = Mockito.mock (ProductService.class);
	
	public ProductControllerTest () {
		this.productController = new ProductController (productService);
	}
	
	@Test
	void getProducts () throws InvalidValueException {
		List <Product> products = new ArrayList <> ();
		
		products.add (new Product (1, "roomba", "description", 12.88f, "https://i.pcmag.com/imagery/reviews/01hmxcWyN13h1LfMglNxHGC-1.fit_scale.size_1028x578.v1589573902.jpg", 12.00f, 10));
		products.add (new Product (2, "roomba", "description", 12.88f, "https://i.pcmag.com/imagery/reviews/01hmxcWyN13h1LfMglNxHGC-1.fit_scale.size_1028x578.v1589573902.jpg", 12.00f, 10));
		
		String query = "";
		int page = 0;
		
		Mockito.when (productService.getProducts (query, page)).thenReturn (products);
		
		assertEquals (ResponseEntity.ok (new JsonResponse ("Got " + products.size () + " products", true, products)), productController.getProducts (query, page));
		
		Mockito.verify (productService).getProducts (query, page);
	}
	
	@Test
	void getProductsWhenSearchQueryIsNull () throws InvalidValueException {
		InvalidValueException exception = assertThrows (InvalidValueException.class, () -> this.productController.getProducts (null, 1));
		
		assertEquals ("Error! Invalid search query", exception.getMessage ());
		
		Mockito.verify (productService, Mockito.never ()).getProducts (Mockito.any (), Mockito.any ());
	}
	
	@Test
	void getProductsWhenPageIsNull () {
		InvalidValueException exception = assertThrows (InvalidValueException.class, () -> productController.getProducts ("", null));
		
		assertEquals ("Error! Invalid page", exception.getMessage ());
		
		Mockito.verify (productService, Mockito.never ()).getProducts (Mockito.any (), Mockito.any ());
	}
	
	@Test
	void getProduct () throws InvalidValueException {
		Product product = new Product (1, "roomba", "description", 12.88f, "https://i.pcmag.com/imagery/reviews/01hmxcWyN13h1LfMglNxHGC-1.fit_scale.size_1028x578.v1589573902.jpg", 12.00f, 10);
		
		Mockito.when (productService.getProduct (product.getId ())).thenReturn (product);
		
		assertEquals (ResponseEntity.ok (new JsonResponse ("Got product", true, product)), productController.getProduct (product.getId ()));
		
		Mockito.verify (productService).getProduct (product.getId ());
	}
	
	@Test
	void getProductNegative () throws InvalidValueException {
		String expectedResult = "Error! Invalid product id";
		String actualResult = null;
		
		Mockito.when (productService.getProduct (99)).thenThrow (new InvalidValueException ("Invalid product id"));
		
		try {
			productService.getProduct (99);
		} catch (InvalidValueException e) {
			actualResult = e.getMessage ();
		}
		
		assertEquals (expectedResult, actualResult);
	}
	
	@Test
	void updateProductSuccessful () throws InvalidValueException {
		MultipartFile file = null;
		Product product = new Product (1, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, null, 13);
		
		Mockito.when (productService.updateProduct (product, file)).thenReturn (product);
		
		Product actual = this.productService.updateProduct (product, file);
		
		assertEquals (product, actual);
	}
	
	@Test
	void updateProductPriceNegative () throws InvalidValueException {
		MultipartFile file = null;
		Product product = new Product (1, "Dog Tricks", "Teach your dog new tricks.", (float) -1.15, null, 13);
		InvalidValueException invalidValueException = new InvalidValueException ("Sale price cannot be higher than normal price.");
		
		Mockito.verify (this.productService, Mockito.times (0)).updateProduct (Mockito.any (), Mockito.any ());
		
	}
	
	@Test
	void updateProductSalePriceTooHigh () throws InvalidValueException {
		MultipartFile file = null;
		Product product = new Product (1, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, null, 13);
		product.setSalePrice (product.getPrice () + 1);
		InvalidValueException invalidValueException = new InvalidValueException ("Sale price cannot be higher than normal price.");
		
		Mockito.when (this.productService.updateProduct (product, file)).thenReturn (product);
		
		Mockito.verify (this.productService, Mockito.times (0)).updateProduct (Mockito.any (), Mockito.any ());
		
	}
}