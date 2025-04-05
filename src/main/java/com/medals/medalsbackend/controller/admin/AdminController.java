package com.medals.medalsbackend.controller.admin;


import com.medals.medalsbackend.dto.AdminCreationDto;
import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AdminNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.service.authorization.AuthorizationService;
import com.medals.medalsbackend.service.authorization.ForbiddenException;
import com.medals.medalsbackend.service.authorization.NoAuthenticationFoundException;
import com.medals.medalsbackend.service.user.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.medals.medalsbackend.controller.BaseController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuthorizationService authorizationService;

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long adminId) throws AdminNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertRoleIn(List.of(UserType.ADMIN));
        adminService.deleteAdmin(adminId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody @Valid AdminCreationDto admin) throws ForbiddenException, NoAuthenticationFoundException, InternalException {
        authorizationService.assertRoleIn(List.of(UserType.ADMIN));
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(Admin.builder()
            .firstName(admin.getFirstName())
            .lastName(admin.getLastName())
            .email(admin.getEmail())
            .type(UserType.ADMIN)
            .build()));
    }
}
