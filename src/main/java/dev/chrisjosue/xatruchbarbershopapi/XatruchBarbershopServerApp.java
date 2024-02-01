package dev.chrisjosue.xatruchbarbershopapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XatruchBarbershopServerApp {
    static void main(String[] args) {
        IO.println("Xatruch Barbershop API Server is running...");
        SpringApplication.run(XatruchBarbershopServerApp.class, args);
    }
}
