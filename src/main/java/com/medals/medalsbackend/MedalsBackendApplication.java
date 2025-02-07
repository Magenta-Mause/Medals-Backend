package com.medals.medalsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MedalsBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(MedalsBackendApplication.class, args);
  }
}
