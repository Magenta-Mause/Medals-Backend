package com.medals.medalsbackend.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.entity.users.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@ConfigurationProperties(prefix = "app.admin")
public class AdminCreationConfiguration  {

    boolean enabled;
    Admin[] admins;

    public AdminCreationConfiguration(boolean enabled, Admin[] admins, String envAdmins) {
        this.enabled = enabled;
        try {
            this.admins = Stream.concat(
                Arrays.stream(admins),
                Arrays.stream(parseJsonToAdmins(envAdmins))
            ).toArray(Admin[]::new);
        } catch (IllegalArgumentException e) {
            log.info("No valid JSON format for Admin array in environment variable, using only the one in the configuration file");
            this.admins = admins;
        }
    }

    public boolean enabled() {
        return enabled;
    }

    public Admin[] admins() {
        return admins;
    }

    private Admin[] parseJsonToAdmins(String adminsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(adminsJson, Admin[].class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format for Admin array", e);
        }
    }
}
