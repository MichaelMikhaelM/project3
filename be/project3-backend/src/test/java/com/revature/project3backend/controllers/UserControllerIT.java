package com.revature.project3backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.jsonmodels.JsonResponse;
import com.revature.project3backend.models.User;
import com.revature.project3backend.services.RoleService;
import com.revature.project3backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserController.class)
class UserControllerIT {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;
	
	@MockBean
	private RoleService roleService;
	
	private final ObjectMapper json = new ObjectMapper ();
	
	//we are passing a string directly because we can't json.write a User object because the password won't get written due to the @JsonAccess on the password field in User
	
	@Test
	void createUserWhenFirstNameIsNull () throws Exception {

		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));

	}
	
	@Test
	void createUserWhenLastNameIsNull () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenEmailIsNull () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenUsernameIsNull () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenPasswordIsNull () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenFirstNameIsEmpty () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenLastNameIsEmpty () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenEmailIsEmpty () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenUsernameIsEmpty () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenPasswordIsEmpty () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenFirstNameIsWhitespace () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"           \",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenLastNameIsWhitespace () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"               \",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenEmailIsWhitespace () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"          \",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenUsernameIsWhitespace () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"         \",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenPasswordIsWhitespace () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"        \"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid user")))));
	}
	
	@Test
	void createUserWhenUsernameIsInvalid () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"@.-/\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid username")))));
	}
	
	@Test
	void createUserWhenEmailIsInvalid () throws Exception {
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"not valid email\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isBadRequest ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse (new InvalidValueException ("Invalid email")))));
	}
	
	@Test
	void createUser () throws Exception {
		User user = new User ();
		
		user.setId (1);
		user.setFirstName ("John");
		user.setLastName ("Smith");
		user.setEmail ("johnsmith@example.com");
		user.setUsername ("johnsmith");
		user.setPassword ("password");
		
		mvc.perform (MockMvcRequestBuilders.post ("/user")
			.contentType (MediaType.APPLICATION_JSON)
			.content ("{\"firstName\": \"John\",\"lastName\": \"Smith\",\"email\": \"johnsmith@example.com\",\"username\": \"johnsmith\",\"password\": \"password\"}"))
			
			.andExpect (MockMvcResultMatchers.status ().isOk ())
			.andExpect (MockMvcResultMatchers.content ().json (json.writeValueAsString (new JsonResponse ("Created user", true, null, "/login"))));
	}
}
