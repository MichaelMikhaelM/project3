package com.revature.project3backend.repositories;

import com.revature.project3backend.models.Product;
import com.revature.project3backend.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.Column;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepoIT {
	@Autowired
	private ProductRepo productRepo;
	
	private final List <Product> products = new ArrayList <> ();
	
	private final int postsPerPage = 20;
	
	@BeforeEach
	public void setUp () {
		products.add (new Product (null, "Java I", "A beginner Java course", 10.00F, "", 8.00F, 5));
		products.add (new Product (null, "Java II", "An intermediate Java course", 20.00F, "", 18.00F, 5));
		products.add (new Product (null, "Python I", "A beginner Python course", 10.00F, "", 8.00F, 5));
		products.add (new Product (null, "Python II", "An intermediate Python course", 20.00F, "", 18.00F, 5));
		
		for (Product product : products) {
			productRepo.save (product);
		}
	}
	
	@AfterEach
	public void tearDown () {
		products.clear ();
		
		productRepo.deleteAll ();
	}
	
	//todo test null and empty search queries?
	
	@Test
	void findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingWithNameInSearchQuery () {
		List <Product> expectedResult = new ArrayList <> ();
		
		expectedResult.add (products.get (2));
		expectedResult.add (products.get (3));
		
		List <Product> actualResult = productRepo.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContaining ("python", "python", PageRequest.of (0, postsPerPage, Sort.by ("name"))).getContent ();
		
		assertEquals (expectedResult, actualResult);
	}
	
	@Test
	void findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingWithDescriptionInSearchQuery () {
		List <Product> expectedResult = new ArrayList <> ();
		
		expectedResult.add (products.get (0));
		expectedResult.add (products.get (2));
		
		List <Product> actualResult = productRepo.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContaining ("beginner", "beginner", PageRequest.of (0, postsPerPage, Sort.by ("name"))).getContent ();
		
		assertEquals (expectedResult, actualResult);
	}
}