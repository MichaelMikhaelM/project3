package com.revature.project3backend.repositories;

import com.revature.project3backend.models.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRoleRepoIT {
    @Autowired
    private UserRoleRepo roleRepo;

    @Test
    void findUserRoleByName() {
    UserRole actual = roleRepo.findUserRoleByName("USER");

    assertEquals( "USER", actual.getRole());
    }
}