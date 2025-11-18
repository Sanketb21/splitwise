package com.splitwise.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * 
 * This service acts as a single entry point for all client requests.
 * It routes requests to appropriate microservices based on the URL path.
 * 
 * @EnableDiscoveryClient - Enables this service to register with Eureka and discover other services
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

