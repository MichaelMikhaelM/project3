package com.revature.project3backend;

import com.revature.project3backend.exceptions.InvalidCredentialsException;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.exceptions.UnauthorizedException;
import com.revature.project3backend.jsonmodels.JsonResponse;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * ApplicationExceptionHandler contains exception handlers for the exceptions that are thrown throughout the application
 */
@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
	/**
	 * This method handles InvalidValueExceptions
	 *
	 * @param exception The exception that was thrown
	 * @return A ResponseEntity used to create the HTTP response
	 */
	@ExceptionHandler
	public ResponseEntity <JsonResponse> invalidValueExceptionHandler (InvalidValueException exception) {
		return ResponseEntity.status (HttpStatus.BAD_REQUEST).body (new JsonResponse (exception));
	}
	
	/**
	 * This method handles UnauthorizedExceptions
	 *
	 * @param exception The exception that was thrown
	 * @return A ResponseEntity used to create the HTTP response
	 */
	@ExceptionHandler
	public ResponseEntity <JsonResponse> unauthorizedExceptionHandler (UnauthorizedException exception) {
		return ResponseEntity.status (HttpStatus.UNAUTHORIZED).body (new JsonResponse (exception, "/login"));
	}
	
	/**
	 * This method handles InvalidCredentialsExceptions
	 *
	 * @param exception The exception that was thrown
	 * @return A ResponseEntity used to create the HTTP response
	 */
	@ExceptionHandler
	public ResponseEntity <JsonResponse> invalidCredentialsExceptionHandler (InvalidCredentialsException exception) {
		return ResponseEntity.status (HttpStatus.UNAUTHORIZED).body (new JsonResponse (exception));
	}
	
	/**
	 * This method handles NotFoundExceptions
	 *
	 * @param exception The exception that was thrown
	 * @return A ResponseEntity used to create the HTTP response
	 */
	@ExceptionHandler
	public ResponseEntity <JsonResponse> notFoundExceptionHandler (ChangeSetPersister.NotFoundException exception) {
		return ResponseEntity.status (HttpStatus.NOT_FOUND).body (new JsonResponse (exception));
	}
	
	/**
	 * This method handles all exceptions that aren't handled by other exception handlers in this class
	 *
	 * @param exception The exception that was thrown
	 * @return A ResponseEntity used to create the HTTP response
	 */
	@ExceptionHandler
	public ResponseEntity <JsonResponse> exceptionHandler (Exception exception) {
		exception.printStackTrace ();
		
		return ResponseEntity.status (HttpStatus.INTERNAL_SERVER_ERROR).body (new JsonResponse (exception));
	}
}
