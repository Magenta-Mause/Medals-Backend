package com.medals.medalsbackend.service.user;

import com.medals.medalsbackend.entity.initializedentity.InitializedEntity;
import com.medals.medalsbackend.entity.initializedentity.InitializedEntityType;
import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AdminNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.repository.InitializedEntityRepository;
import com.medals.medalsbackend.service.websockets.AdminWebsocketMessageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminCreationConfiguration.class)
public class AdminService {

    private final UserEntityService userEntityService;
    private final AdminCreationConfiguration adminCreationConfiguration;
    private final InitializedEntityRepository initializedEntityRepository;
    private final AdminWebsocketMessageService adminWebsocketMessageService;

    @SneakyThrows
    @Profile("!test")
    @EventListener(ApplicationReadyEvent.class)
    public void initiateAdmin() {
        if (!adminCreationConfiguration.enabled()) {
            return;
        }
        if (initializedEntityRepository.existsById(InitializedEntityType.Admin)) {
            log.info("Admin already initiated");
            return;
        }

        log.info("Initializing admin");
        if (adminCreationConfiguration.admins().length > 0) {
            Arrays.stream(adminCreationConfiguration.admins()).toList().forEach(admin -> {
                try {
                    createAdmin(admin);
                } catch (InternalException e) {
                    throw new RuntimeException(e);
                }
            });
            initializedEntityRepository.save(new InitializedEntity(InitializedEntityType.Admin));
        }
        log.info("Initiated {} admins", adminCreationConfiguration.admins().length);
    }

    public Admin createAdmin(Admin admin) throws InternalException {
        admin.setId(null);
        Admin createdAdmin = (Admin) userEntityService.save(admin.getEmail(), admin);
        adminWebsocketMessageService.sendAdminCreate(createdAdmin);
        return createdAdmin;
    }

    public void deleteAdmin(Long adminId) throws Exception {
        log.info("Executing delete admin by id {}", adminId);
        userEntityService.assertUserType(adminId, UserType.ADMIN, AdminNotFoundException.fromAdminId(adminId));
        userEntityService.deleteById(adminId);
        adminWebsocketMessageService.sendAdminDelete(adminId);
    }

    public List<Admin> getAllAdmins() {
        return userEntityService.getAllAdmins();
    }

    public Admin updateAdmin(Long adminId, String firstName, String lastName) throws AdminNotFoundException {
        log.info("Updating admin with id {}", adminId);
        Optional<Admin> adminOptional = userEntityService.findAdminById(adminId);
        if (adminOptional.isEmpty()) {
            throw AdminNotFoundException.fromAdminId(adminId);
        }

        Admin admin = adminOptional.get();
        admin.setFirstName(firstName);
        admin.setLastName(lastName);

        Admin updatedAdmin = (Admin) userEntityService.update(admin);
        adminWebsocketMessageService.sendAdminUpdate(updatedAdmin);

        return updatedAdmin;
    }
}