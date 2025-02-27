package com.medals.medalsbackend.exception;

import org.springframework.http.HttpStatus;

public class AdminNotFoundException extends GenericAPIRequestException {
    AdminNotFoundException(Long adminId){
        super("Admin with id not found [id: " + adminId + "]", HttpStatus.NOT_FOUND);
    }

    public static AdminNotFoundException fromAdminId(Long adminId){
        return new AdminNotFoundException(adminId);
    }
}
