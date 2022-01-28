package com.revature.project3backend.exceptions;

/**
 * InvalidCredentialsException is thrown when invalid credentials are attempted to be used to log in
 */
public class InvalidCredentialsException extends Exception {
	/**
	 * This constructor sets the message of the exception
	 */
	public InvalidCredentialsException () {
		super ("Error! Invalid credentials");
	}
}
