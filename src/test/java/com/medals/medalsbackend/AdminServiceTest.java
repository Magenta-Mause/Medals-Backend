package com.medals.medalsbackend;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.service.user.AdminService;
import com.medals.medalsbackend.service.user.UserEntityService;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
        assertEquals("testEmail@gmail.com", createdAdmin.getEmail());
        assertEquals("firstName", createdAdmin.getFirstName());
        assertEquals("lastName", createdAdmin.getLastName());
    }
}
