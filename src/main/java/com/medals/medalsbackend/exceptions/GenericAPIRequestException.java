package com.medals.medalsbackend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
@AllArgsConstructor
public class GenericAPIRequestException extends Exception {
    private String message;
    private HttpStatusCode statusCode;
}
