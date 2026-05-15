package com.gym.system.workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class GymSystemWorloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymSystemWorloadApplication.class, args);
    }

}
