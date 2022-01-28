package com.revature.project3backend.controllers;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.modeldtos.UserDTO;
import com.revature.project3backend.models.User;
import com.revature.project3backend.models.UserRole;
import com.revature.project3backend.services.RoleService;
import com.revature.project3backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController handles requests concerning users
 */
@RestController
@RequestMapping ("user")
@CrossOrigin (origins = "http://localhost:4200/", allowCredentials = "true")
public class UserController {
	/**
	 * The instance of UserService to use
	 */
	private final UserService userService;
	
	/**
	 * The instance of RoleService to use
	 */
	private final RoleService roleService;
	
	/**
	 * This constructor is automatically called by Spring
	 *
	 * @param userService The instance of UserService to use
	 * @param roleService The instance of RoleService to use
	 */
	@Autowired
	public UserController (UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}

	/**
	 * Creates a user
	 *
	 * @param body The data to use to create the user, contains a first name, last name, email, username, and password
	 * @return A ResponseEntity used to create the HTTP response, redirects to /login
	 * @throws InvalidValueException Thrown when validation fails
	 */
	@PostMapping
	public ResponseEntity <JsonResponse> createUser (@RequestBody UserDTO body) throws InvalidValueException {

		
		if (body.getFirstName () == null || body.getLastName () == null || body.getEmail () == null || body.getUsername () == null || body.getPassword () == null) {
			throw new InvalidValueException ("Invalid user");
		}
		
		if (body.getFirstName ().trim ().equals ("") || body.getLastName ().trim ().equals ("") || body.getEmail ().trim ().equals ("") || body.getUsername ().trim ().equals ("") || body.getPassword ().trim ().equals ("")) {
			throw new InvalidValueException ("Invalid user");
		}
		
		if (!body.getUsername ().matches ("^[\\w-]+$")) {
			throw new InvalidValueException ("Invalid username");
		}
		
		if (!body.getEmail ().matches ("^[\\w-\\.]+@[\\w-]+\\.[a-zA-z]+$")) {
			throw new InvalidValueException ("Invalid email");
		}
		
		//create user
		
		UserRole role = this.roleService.getRoleByName ("USER");
		
		body.setRole (role);
		
		userService.createUser ((User) body);
		
		return ResponseEntity.ok (new JsonResponse ("Created user", true, null, "/login"));
	}
}
