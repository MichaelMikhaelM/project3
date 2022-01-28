package com.revature.project3backend.services;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.repositories.ProductRepo;
import com.revature.project3backend.utils.FileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductServiceTest {
	ProductRepo productRepo = Mockito.mock (ProductRepo.class);
	ProductService productService;
	MultipartFile mf = null;
	
	List <Product> products = new ArrayList <> ();

	@BeforeAll
	static void beforeAll(){
		Mockito.mockStatic(FileUtil.class);

	}

	@BeforeEach
	void beforeEach(){

		mf = new MultipartFile() {
			@Override
			public String getName() {
				return null;
			}

			@Override
			public String getOriginalFilename() {
				return "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png";
			}

			@Override
			public String getContentType() {
				return null;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public long getSize() {
				return 0;
			}

			@Override
			public byte[] getBytes() throws IOException {
				return new byte[0];
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return null;
			}

			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {

			}
		};
	}
	
	public ProductServiceTest () {
		this.productService = new ProductService (this.productRepo);
		
		products.add (new Product (1, "name", "description", 10F, "", null, 10));
		products.add (new Product (2, "name", "description", 10F, "", null, 10));
		products.add (new Product (3, "name", "description", 10F, "", null, 10));
	}
	
	@Test
	void getProducts () {
		String searchQuery = "query";
		
		Page <Product> page = new PageImpl <> (products);
		
		Mockito.when (productRepo.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContaining (Mockito.eq (searchQuery), Mockito.eq (searchQuery), Mockito.any (PageRequest.class))).thenReturn (page);
		
		assertEquals (products, productService.getProducts (searchQuery, 0));
	}
	
	@Test
	void getProduct () throws InvalidValueException {
		int id = 1;
		
		Mockito.when (productRepo.findById (id)).thenReturn (Optional.of (products.get (0)));
		
		assertEquals (products.get (0), productService.getProduct (id));
	}
	
	@Test
	void getProductWhenNotFound () throws InvalidValueException {
		int id = 1;
		
		Mockito.when (productRepo.findById (id)).thenReturn (Optional.empty ());
		
		InvalidValueException exception = assertThrows (InvalidValueException.class, () -> productService.getProduct (id));
		
		assertEquals ("Error! Invalid product id", exception.getMessage ());
	}
	
	@Test
	void reduceStock () throws InvalidValueException {
		int stock = 10;
		
		Product product = new Product (3, "name", "description", 10F, "", null, stock);
		
		int quantity = 3;
		
		productService.reduceStock (product, quantity);
		
		assertEquals (stock - quantity, product.getStock ());
		
		Mockito.verify (productRepo).save (product);
	}
	
	@Test
	void reduceStockWhenStockWouldBeBelowZero () throws InvalidValueException {
		int stock = 2;
		
		Product product = new Product (3, "name", "description", 10F, "", null, stock);
		
		int quantity = 3;
		
		InvalidValueException exception = assertThrows (InvalidValueException.class, () -> productService.reduceStock (product, quantity));
		
		assertEquals ("Error! Invalid quantity", exception.getMessage ());
		
		assertEquals (stock, product.getStock ());
		
		Mockito.verify (productRepo, Mockito.never ()).save (Mockito.any ());
	}

	@Test
	void createProductWithValidInputAndFile() throws InvalidValueException {
		Product product = new Product (1, "name", "description", 10F, null, null, null);

		Mockito.when(FileUtil.uploadToS3(product, this.mf)).thenReturn("https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png");
		Mockito.when(productRepo.save(product)).thenReturn(product);

		Product actualResult = productService.createProduct(product, this.mf);
		product.setImageUrl("https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png");

		assertEquals(product, actualResult);
	}

	@Test
	void createProductWithValidInputNoFile() throws InvalidValueException {
		Product product = new Product (1, "name", "description", 10F, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", null, null);

		Mockito.when(productRepo.save(product)).thenReturn(product);

		Product actualResult = productService.createProduct(product, null);

		assertEquals(product, actualResult);
	}

	@Test
	void createProductWithNegativePrice() throws InvalidValueException {
		Product product = new Product (1, "name", "description", -(10F), "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", null, null);

		String expectedResult = "Error! Price cannot be less than 0";
		String actualResult = null;

		try {
			productService.createProduct(product, null);
		} catch (InvalidValueException e) {
			actualResult = e.getMessage();
		}

		assertEquals(expectedResult, actualResult);
	}

	@Test
	void createProductWithNegativeSalePrice() throws InvalidValueException {
		Product product = new Product (1, "name", "description", 10F, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", -(10F), null);

		String expectedResult = "Error! Sale price cannot be less than 0";
		String actualResult = null;

		try {
			productService.createProduct(product, null);
		} catch (InvalidValueException e) {
			actualResult = e.getMessage();
		}

		assertEquals(expectedResult, actualResult);
	}

	@Test
	void createProductWithSalePriceGreaterThanPrice() throws InvalidValueException {
		Product product = new Product (1, "name", "description", 10F, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", 40F, null);

		String expectedResult = "Error! Sales price cannot be greater than original price";
		String actualResult = null;

		try {
			productService.createProduct(product, null);
		} catch (InvalidValueException e) {
			actualResult = e.getMessage();
		}

		assertEquals(expectedResult, actualResult);
	}

	@Test
	void updateProductWithFilePositive() throws InvalidValueException {
		Product product = new Product (1, "name", "description", 10F, null, null, null);

		Mockito.when(FileUtil.uploadToS3(product, this.mf)).thenReturn("https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png");
		Mockito.when(productRepo.save(product)).thenReturn(product);

		Product actualResult = productService.updateProduct(product, this.mf);
		product.setImageUrl("https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png");

		assertEquals(product, actualResult);
	}

	@Test
	void updateProductWithoutFilePositive() throws InvalidValueException {
		Product product = new Product (1, "name", "description", 10F, null, null, null);

		Mockito.when(FileUtil.uploadToS3(product, null)).thenReturn("https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png");
		Mockito.when(productRepo.save(product)).thenReturn(product);

		Product actualResult = productService.updateProduct(product, null);

		assertEquals(product, actualResult);
	}

	@Test
	void updateProductWithoutNameNegative() throws InvalidValueException {
		Product product = new Product (1, null, "description", 10F, null, null, null);

		Mockito.when(FileUtil.uploadToS3(product, null)).thenReturn("https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png");
		Mockito.when(productRepo.save(product)).thenReturn(product);

		String expectedResult = "Error! No product name";

		String actualResult = new String();

		try {
			productService.updateProduct(product, this.mf);
		} catch (InvalidValueException e) {
			actualResult = e.getMessage();
		}

		assertEquals(expectedResult, actualResult);
	}

	@Test
	void updateProductWithoutDescriptionNegative() throws InvalidValueException {
		Product product = new Product (1, "name", null, 10F, null, null, null);

		Mockito.when(FileUtil.uploadToS3(product, null)).thenReturn("https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png");
		Mockito.when(productRepo.save(product)).thenReturn(product);

		String expectedResult = "Error! No product description";

		String actualResult = new String();

		try {
			productService.updateProduct(product, this.mf);
		} catch (InvalidValueException e) {
			actualResult = e.getMessage();
		}

		assertEquals(expectedResult, actualResult);
	}


}