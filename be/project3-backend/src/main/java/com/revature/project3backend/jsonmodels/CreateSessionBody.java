package com.revature.project3backend.jsonmodels;

import lombok.Data;

/**
 * CreateSessionBody is used as the body for the method in SessionController that creates a session
 */
@Data
public class CreateSessionBody {
	/**
	 * The username to log in with
	 */
	private String identifier;
	
	/**
	 * The password to log in with
	 */
	private String password;
}
