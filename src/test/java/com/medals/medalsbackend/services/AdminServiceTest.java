package com.medals.medalsbackend.services;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.service.user.AdminService;
import com.medals.medalsbackend.service.user.UserEntityService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class AdminServiceTest {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserEntityService userEntityService;

    @SneakyThrows
    @Test
    public void testAdminCreation() {
        Admin admin = adminService.createAdmin(Admin.builder().lastName("lastName").firstName("firstName").email("testEmail@gmail.com").build());
        Admin createdAdmin = (Admin) userEntityService.findById(admin.getId()).get();
        assertEquals(UserType.ADMIN, createdAdmin.getType());
        assertEquals("testemail@gmail.com", createdAdmin.getEmail());
        assertEquals("firstName", createdAdmin.getFirstName());
        assertEquals("lastName", createdAdmin.getLastName());
    }
}
