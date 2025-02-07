package com.medals.medalsbackend.service.user;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.exceptions.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminCreationConfiguration.class)
public class AdminService {

    private final UserEntityService userEntityService;
    private final AdminCreationConfiguration adminCreationConfiguration;
    private final Environment environment;

    @SneakyThrows
    @Profile("!test")
    @EventListener(ApplicationReadyEvent.class)
    public void initiateAdmin() {
        if (!adminCreationConfiguration.enabled()) {
            return;
        }

        log.info("Initializing admin");
        Admin admin = createAdmin(Admin.builder()
                .email(adminCreationConfiguration.adminEmail())
                .firstName(adminCreationConfiguration.adminFirstName())
                .lastName(adminCreationConfiguration.adminLastName())
                .build());
        log.info("Initiated admin: {}", admin);
    }

    public Admin createAdmin(Admin admin) throws InternalException {
        return (Admin) userEntityService.save(admin.getEmail(), admin);
    }
}
