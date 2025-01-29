package com.medals.medalsbackend.exceptions;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
public class GenericAPIRequestException extends Exception {
    private String message;
    private HttpStatusCode statusCode;
}
