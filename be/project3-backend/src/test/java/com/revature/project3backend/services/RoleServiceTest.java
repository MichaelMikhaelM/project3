package com.revature.project3backend.services;

import com.revature.project3backend.models.UserRole;
import com.revature.project3backend.repositories.UserRoleRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RoleServiceTest {

    UserRoleRepo roleRepo = Mockito.mock(UserRoleRepo.class);
    RoleService roleService;

    @Autowired
    public RoleServiceTest(){
        this.roleService = new RoleService(roleRepo);
    }

    @Test
    void getRoleByName() {
        UserRole role = new UserRole(1, "USER");

        Mockito.when(this.roleRepo.findUserRoleByName(role.getRole())).thenReturn(role);

        UserRole actual = this.roleService.getRoleByName("USER");

        assertEquals(role, actual);
    }
}