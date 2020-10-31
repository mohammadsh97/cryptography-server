package com.cryptographyServer.cryptography.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// The Swagger-ui console on localhost: http://localhost:8081/swagger-ui/
// The H2 console on localhost: http://localhost:8081/h2-console/

@SpringBootApplication
public class CryptographyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptographyServerApplication.class, args);
    }
}