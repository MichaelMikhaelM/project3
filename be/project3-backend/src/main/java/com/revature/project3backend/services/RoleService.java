package com.revature.project3backend.services;

import com.revature.project3backend.models.UserRole;
import com.revature.project3backend.repositories.UserRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
/**
 * This service deals with user roles, such as ADMIN or USER.
 */ public class RoleService {
	
	private UserRoleRepo roleRepo;
	
	@Autowired
	public RoleService (UserRoleRepo roleRepo) {
		this.roleRepo = roleRepo;
	}
	
	/**
	 * Returns the role object for a given role name.
	 *
	 * @param roleName The name of the role to search for, all names should be uppercase.
	 * @return The UserRole object that matches the name.
	 */
	public UserRole getRoleByName (String roleName) {
		return this.roleRepo.findUserRoleByName (roleName.toUpperCase ());
	}
}
