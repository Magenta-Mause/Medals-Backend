package com.medals.medalsbackend.controller.admin;


import com.medals.medalsbackend.exception.AdminNotFoundException;
import com.medals.medalsbackend.service.user.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.medals.medalsbackend.controller.BaseController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/admins")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@DeleteMapping("/{adminId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Void> deleteAdmin(@PathVariable Long adminId) throws AdminNotFoundException {
		adminService.deleteAdmin(adminId);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
	}
}
