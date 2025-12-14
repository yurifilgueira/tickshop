package com.tickshop.tickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TicketsApplication {

    static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }

}
