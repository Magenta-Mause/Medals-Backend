package com.medals.medalsbackend.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.config.HttpStatusCaptureFilter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    @ResponseBody
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse) {
            return body;
        }

        String endpoint = request.getURI().getPath();
        Integer httpStatusCode = HttpStatusCaptureFilter.getHttpStatus();
        httpStatusCode = httpStatusCode == null ? 500 : httpStatusCode;
        ApiResponse<Object> renderedResponse = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatusCode)
                .message(ApiStatus.fromCode(httpStatusCode).toString())
                .httpStatus(HttpStatusCode.valueOf(httpStatusCode))
                .endpoint(endpoint)
                .data(body)
                .build();

        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(renderedResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return renderedResponse;
    }
}
