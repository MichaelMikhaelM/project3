package com.revature.project3backend.repositories;

import com.revature.project3backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRepo is the repository for users
 */
public interface UserRepo extends JpaRepository <User, Integer> {
	/**
	 * Gets a user given their username
	 *
	 * @param username The username to use to get the user
	 * @return The found user
	 */
	User findByUsername (String username);
	
	/**
	 * Gets a user given their email
	 *
	 * @param email The email to use to get the user
	 * @return The found user
	 */
	User findByEmail (String email);
}
