package com.medals.medalsbackend.api;

import com.medals.medalsbackend.config.HttpStatusCaptureFilter;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    @ResponseBody
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String endpoint = request.getURI().getPath();
        Integer httpStatusCode = HttpStatusCaptureFilter.getHttpStatus();

        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatusCode)
                .message(ApiStatus.fromCode(httpStatusCode).toString())
                .httpStatus(HttpStatusCode.valueOf(httpStatusCode))
                .endpoint(endpoint)
                .data(body)
                .build();
    }
}
