package com.gym.system.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@CucumberContextConfiguration
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
