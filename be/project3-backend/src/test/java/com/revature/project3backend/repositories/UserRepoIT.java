package com.revature.project3backend.repositories;

import com.revature.project3backend.models.User;
import com.revature.project3backend.models.UserRole;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
public class UserRepoIT {
	@Autowired
	private UserRepo userRepo;
	
	private final List <User> users = new ArrayList <> ();
	
	@BeforeEach
	public void setUp () {
		UserRole role = new UserRole(1, "Admin");

		users.add (new User (null, "User", "1", "email1", "username1", "password", new ArrayList <> (), new ArrayList <> (), role));
		users.add (new User (null, "User", "2", "email2", "username2", "password", new ArrayList <> (), new ArrayList <> (), role));
		users.add (new User (null, "User", "3", "email3", "username3", "password", new ArrayList <> (), new ArrayList <> (), role));
		
		for (User user : users) {
			userRepo.save (user);
		}
	}
	
	@AfterEach
	public void tearDown () {
		users.clear ();
		
		userRepo.deleteAll ();
	}
	
	//todo test null username or email?
	
	@Test
	void findByUsername () {
		assertEquals (users.get (0), userRepo.findByUsername (users.get (0).getUsername ()));
	}
	
	@Test
	void findByUsernameWhenNotFound () {
		assertNull (userRepo.findByUsername ("wrong username"));
	}
	
	@Test
	void findByEmail () {
		assertEquals (users.get (0), userRepo.findByEmail (users.get (0).getEmail ()));
	}
	
	@Test
	void findByEmailWhenNotFound () {
		assertNull (userRepo.findByEmail ("wrong email"));
	}
}
