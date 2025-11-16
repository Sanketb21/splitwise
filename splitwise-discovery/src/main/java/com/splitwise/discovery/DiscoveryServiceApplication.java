package com.splitwise.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Discovery Service Application
 * 
 * This service acts as a service registry where all microservices will register themselves.
 * Other services can discover and communicate with each other through this registry.
 * 
 * @EnableEurekaServer - Enables this application to act as a Eureka Server
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}

