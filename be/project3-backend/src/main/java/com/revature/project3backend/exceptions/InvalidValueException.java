package com.revature.project3backend.exceptions;

/**
 * InvalidValueException is thrown when there's an issue caused by a value given by the user
 */
public class InvalidValueException extends Exception {
	/**
	 * This constructor sets the message of the exception
	 *
	 * @param message The message to use
	 */
	public InvalidValueException (String message) {
		super ("Error! " + message);
	}
}
