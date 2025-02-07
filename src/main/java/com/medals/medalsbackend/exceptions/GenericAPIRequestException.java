package com.medals.medalsbackend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatusCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
public class GenericAPIRequestException extends Exception {
    private String message;
    private HttpStatusCode statusCode;
}
