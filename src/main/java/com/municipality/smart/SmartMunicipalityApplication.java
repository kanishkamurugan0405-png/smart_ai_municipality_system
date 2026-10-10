package com.municipality.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartMunicipalityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMunicipalityApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  SMART MUNICIPALITY - AI CIVIC PLATFORM RUNNING ON PORT 8080    ");
        System.out.println("  Portal: http://localhost:8080                                  ");
        System.out.println("==================================================================");
    }
}
