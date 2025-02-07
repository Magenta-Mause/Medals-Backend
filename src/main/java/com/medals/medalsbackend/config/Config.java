package com.medals.medalsbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.medals.medalsbackend.security.config.JwtConfigurationProperties;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(JwtConfigurationProperties.class)
public class Config {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Key getSigningKey(JwtConfigurationProperties jwtConfigurationProperties) {
        byte[] keyBytes = Base64.getDecoder().decode(jwtConfigurationProperties.secretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    public JwtParser jwtParser(Key jwtSecretKey) {
        return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build();
    }
}
