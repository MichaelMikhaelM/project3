package com.revature.project3backend.controllers;

import com.revature.project3backend.exceptions.InvalidCredentialsException;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.jsonmodels.CreateSessionBody;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.models.User;
import com.revature.project3backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * SessionController handles requests concerning sessions
 */
@RestController
@RequestMapping ("session")
@CrossOrigin (origins = "http://localhost:4200/", allowCredentials = "true")
public class SessionController {
	/**
	 * The instance of UserService to use
	 */
	private final UserService userService;
	
	/**
	 * This constructor is automatically called by Spring
	 *
	 * @param userService The instance of UserService to use
	 */
	@Autowired
	public SessionController (UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * Creates a session for the user (logs them in)
	 *
	 * @param body The data to use to log in the user, contains an identifier and a password
	 * @param httpSession The HTTP session of the user
	 * @return A ResponseEntity used to create the HTTP response, redirects to /
	 * @throws InvalidCredentialsException Thrown when credentials are invalid
	 * @throws InvalidValueException Thrown when validation fails
	 */
	@PostMapping
	public ResponseEntity <JsonResponse> createSession (@RequestBody CreateSessionBody body, HttpSession httpSession) throws InvalidCredentialsException, InvalidValueException {
		if (body.getIdentifier () == null || body.getPassword () == null) {
			throw new InvalidValueException ("Invalid credentials");
		}
		
		User user = this.userService.loginUser (body.getIdentifier (), body.getPassword ());
		
		httpSession.setAttribute ("user", user);
		
		return ResponseEntity.ok (new JsonResponse ("Logged in", true, user, "/"));
	}
	
	/**
	 * Deletes the user's session (logs them out)
	 *
	 * @param httpSession The HTTP session of the user
	 * @return A ResponseEntity used to create the HTTP response, redirects to /login
	 */
	@DeleteMapping
	public ResponseEntity <JsonResponse> deleteSession (HttpSession httpSession) {
		httpSession.invalidate ();
		
		return ResponseEntity.ok (new JsonResponse ("Logged out", true, null, "/login"));
	}
}
