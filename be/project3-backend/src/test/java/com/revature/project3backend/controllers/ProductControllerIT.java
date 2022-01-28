package com.revature.project3backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.exceptions.UnauthorizedException;
import com.revature.project3backend.jsonmodels.CreateSessionBody;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.models.*;
import com.revature.project3backend.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcTest(ProductController.class)
public class ProductControllerIT {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private MockHttpSession session;

    private final ObjectMapper json = new ObjectMapper ();
	
	@Test
	void getProducts () throws Exception {
		List <Product> products = new ArrayList <> ();
		
		products.add (new Product (1, "roomba", "description", 12.88f, "https://i.pcmag.com/imagery/reviews/01hmxcWyN13h1LfMglNxHGC-1.fit_scale.size_1028x578.v1589573902.jpg", 12.00f, 10));
		products.add (new Product (2, "roomba", "description", 12.88f, "https://i.pcmag.com/imagery/reviews/01hmxcWyN13h1LfMglNxHGC-1.fit_scale.size_1028x578.v1589573902.jpg", 12.00f, 10));
		
		String searchQuery = "roomba";
		int page = 0;
		
		Mockito.when (productService.getProducts (searchQuery, page)).thenReturn (products);
		
		mvc.perform (MockMvcRequestBuilders.get ("/product?searchQuery=" + searchQuery + "&page=" + page))
			.andExpect (MockMvcResultMatchers.status ().isOk ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse ("Got " + products.size () + " products", true, products))));
		
		Mockito.verify (productService).getProducts (searchQuery, page);
	}
	
	@Test
	void getProduct () throws Exception {
		Product product = new Product (1, "roomba", "description", 12.88f, "https://i.pcmag.com/imagery/reviews/01hmxcWyN13h1LfMglNxHGC-1.fit_scale.size_1028x578.v1589573902.jpg", 12.00f, 10);
		
		Mockito.when (productService.getProduct (product.getId ())).thenReturn (product);
		
		mvc.perform (MockMvcRequestBuilders.get ("/product/" + product.getId ()))
			.andExpect (MockMvcResultMatchers.status ().isOk ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse ("Got product", true, product))));
		
		Mockito.verify (productService).getProduct (product.getId ());
	}
	


    @Test
    void updateProductSuccessful() throws Exception {
		MultipartFile file = null;
        Product product = new Product(1, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, null, 13);

        Mockito.when(this.productService.updateProduct(product, file)).thenReturn(product);
	
		List <CartItem> items = new ArrayList<>();
		List <Transaction> transactions = new ArrayList<>();
		String pass = "pass123";
		UserRole role = new UserRole (1, "ADMIN");
	
		User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", pass, items, transactions, role);
		Mockito.when(session.getAttribute ("user")).thenReturn(user);
		
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/product")
                .param("id", product.getId().toString())
                .param("name", product.getName())
                .param("description", product.getDescription())
                .param("price", product.getPrice().toString())
                .param("stock", product.getStock().toString())
                .contentType(MediaType.MULTIPART_FORM_DATA)
				.session (session);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper()
                        .writeValueAsString(new JsonResponse ("Product updated ok.", true, product, "/product/1"))));
    }

	@Test
	void updateProductNotLoggedIn() throws Exception {
		MultipartFile file = null;
		Product product = new Product(1, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, null, 13);
		UnauthorizedException exception = new UnauthorizedException();

		Mockito.when(this.productService.updateProduct(product, file)).thenReturn(product);

		Mockito.when(session.getAttribute ("user")).thenReturn(null);

		RequestBuilder request = MockMvcRequestBuilders
				.patch("/product")
				.param("id", product.getId().toString())
				.param("name", product.getName())
				.param("description", product.getDescription())
				.param("price", product.getPrice().toString())
				.param("stock", product.getStock().toString())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.session (session);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.content().json(new ObjectMapper()
						.writeValueAsString(new JsonResponse (exception, "/login"))));
	}


	@Test
	void updateProductAsNotAdmin() throws Exception {
		MultipartFile file = null;
		Product product = new Product(1, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, null, 13);
		UnauthorizedException exception = new UnauthorizedException();

		Mockito.when(this.productService.updateProduct(product, file)).thenReturn(product);

		List <CartItem> items = new ArrayList<>();
		List <Transaction> transactions = new ArrayList<>();
		String pass = "pass123";
		UserRole role = new UserRole (1, "USER");

		User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", pass, items, transactions, role);
		Mockito.when(session.getAttribute ("user")).thenReturn(user);

		RequestBuilder request = MockMvcRequestBuilders
				.patch("/product")
				.param("id", product.getId().toString())
				.param("name", product.getName())
				.param("description", product.getDescription())
				.param("price", product.getPrice().toString())
				.param("stock", product.getStock().toString())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.session (session);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.content().json(new ObjectMapper()
						.writeValueAsString(new JsonResponse (exception, "/login"))));
	}

    @Test
    void updateProductNegativePrice() throws Exception {
        MultipartFile file = null;
        Product product = new Product(1, "Dog Tricks", "Teach your dog new tricks.", (float) -1.15, null, 13);
        InvalidValueException invalidValueException = new InvalidValueException("Price cannot be negative.");
        Mockito.when(this.productService.updateProduct(product, file)).thenReturn(product);
	
		List <CartItem> items = new ArrayList<>();
		List <Transaction> transactions = new ArrayList<>();
		String pass = "pass123";
		UserRole role = new UserRole (1, "ADMIN");
	
		User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", pass, items, transactions, role);
		Mockito.when(session.getAttribute ("user")).thenReturn(user);
		
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/product")
                .param("id", product.getId().toString())
                .param("name", product.getName())
                .param("description", product.getDescription())
                .param("price", product.getPrice().toString())
                .param("stock", product.getStock().toString())
                .contentType(MediaType.MULTIPART_FORM_DATA)
				.session (session);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper()
                        .writeValueAsString(new JsonResponse (invalidValueException))));
    }

    @Test
    void updateProductSalePriceTooHigh() throws Exception {
        MultipartFile file = null;
        Product product = new Product(1, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, null, 13);
        InvalidValueException invalidValueException = new InvalidValueException("Sale price cannot be higher than normal price.");
        Mockito.when(this.productService.updateProduct(product, file)).thenReturn(product);
	
		List <CartItem> items = new ArrayList<>();
		List <Transaction> transactions = new ArrayList<>();
		String pass = "pass123";
		UserRole role = new UserRole (1, "ADMIN");
	
		User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", pass, items, transactions, role);
		Mockito.when(session.getAttribute ("user")).thenReturn(user);
		
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/product")
                .param("id", product.getId().toString())
                .param("name", product.getName())
                .param("description", product.getDescription())
                .param("price", product.getPrice().toString())
                .param("stock", product.getStock().toString())
                .param("salePrice", (product.getPrice() + 10) + "")
                .contentType(MediaType.MULTIPART_FORM_DATA)
				.session (session);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper()
                        .writeValueAsString(new JsonResponse (invalidValueException))));
    }
	
    @Test
    void createProduct() throws Exception {
        MultipartFile file = null;
        List <CartItem> items = new ArrayList<>();
        List <Transaction> transactions = new ArrayList<>();
        String pass = "pass123";
        UserRole role = new UserRole (1, "ADMIN");
        Product product = new Product(null, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", 13);
        Product integral = new Product(9, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", 13);

        User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", pass, items, transactions, role);
        Mockito.when(session.getAttribute ("user")).thenReturn(user);
        Mockito.when(this.productService.createProduct(product, file)).thenReturn(integral);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/product")
                .param("name", product.getName())
                .param("description", product.getDescription())
                .param("price", product.getPrice().toString())
                .param("stock", product.getStock().toString())
                .param("imageUrl", product.getImageUrl())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .session(session);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper()
                        .writeValueAsString (new JsonResponse ("Got product updated ok.", true, integral, "/product/" + integral.getId()))));
    }
	
	@Test
	void createProductWhenNotLoggedIn () throws Exception {

		MultipartFile file = null;
		List <CartItem> items = new ArrayList<>();
		List <Transaction> transactions = new ArrayList<>();
		String pass = "pass123";
		UserRole role = new UserRole (1, "ADMIN");
		Product product = new Product(null, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", 13);
		Product integral = new Product(9, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", 13);

		User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", pass, items, transactions, role);
		Mockito.when(session.getAttribute ("user")).thenReturn(null);
		Mockito.when(this.productService.createProduct(product, file)).thenReturn(integral);
		RequestBuilder request = MockMvcRequestBuilders
				.post("/product")
				.param("name", product.getName())
				.param("description", product.getDescription())
				.param("price", product.getPrice().toString())
				.param("stock", product.getStock().toString())
				.param("imageUrl", product.getImageUrl())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.session(session);

		UnauthorizedException unauthorizedException = new UnauthorizedException();

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.content().json(new ObjectMapper()
						.writeValueAsString (new JsonResponse ("Error! Unauthorized", false, null,"/login"))));
	}
	
	@Test
	void createProductWhenNotAnAdmin () throws Exception {
		MultipartFile file = null;
		List <CartItem> items = new ArrayList<>();
		List <Transaction> transactions = new ArrayList<>();
		String pass = "pass123";
		UserRole role = new UserRole (2, "USER");
		Product product = new Product(null, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", 13);
		Product integral = new Product(9, "Dog Tricks", "Teach your dog new tricks.", (float) 1.15, "https://s3-alpha.figma.com/hub/file/948140848/1f4d8ea7-e9d9-48b7-b70c-819482fb10fb-cover.png", 13);

		User user = new User (1, "john", "doe", "jdoe@mail.com", "jdoe1", pass, items, transactions, role);
		Mockito.when(session.getAttribute ("user")).thenReturn(user);
		Mockito.when(this.productService.createProduct(product, file)).thenReturn(integral);
		RequestBuilder request = MockMvcRequestBuilders
				.post("/product")
				.param("name", product.getName())
				.param("description", product.getDescription())
				.param("price", product.getPrice().toString())
				.param("stock", product.getStock().toString())
				.param("imageUrl", product.getImageUrl())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.session(session);

		UnauthorizedException unauthorizedException = new UnauthorizedException();

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.content().json(new ObjectMapper()
						.writeValueAsString (new JsonResponse ("Error! Unauthorized", false, null,"/login"))));
	}
}


