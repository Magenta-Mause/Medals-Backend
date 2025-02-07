package com.medals.medalsbackend.service.user;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.exceptions.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminCreationConfiguration.class)
public class AdminService {

    private final UserEntityService userEntityService;
    private final AdminCreationConfiguration adminCreationConfiguration;
    private final Environment environment;

    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void initiateAdmin() {
        if (!adminCreationConfiguration.enabled() || Arrays.stream(environment.getActiveProfiles()).toList().contains("test")) {
            return;
        }

        createAdmin(Admin.builder()
                .email(adminCreationConfiguration.adminEmail())
                .firstName(adminCreationConfiguration.adminFirstName())
                .lastName(adminCreationConfiguration.adminLastName())
                .build()
        );
    }

    public Admin createAdmin(Admin admin) throws InternalException {
        return (Admin) userEntityService.save(admin.getEmail(), admin);
    }
}
