package com.revature.project3backend.exceptions;

/**
 * UnauthorizedException is thrown when the user is unauthorized to do an action
 */
public class UnauthorizedException extends Exception {
	/**
	 * This constructor sets the message of the exception
	 */
	public UnauthorizedException () {
		super ("Error! Unauthorized");
	}
}
