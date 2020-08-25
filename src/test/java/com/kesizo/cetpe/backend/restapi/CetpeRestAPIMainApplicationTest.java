package com.kesizo.cetpe.backend.restapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
//This annotation tells SpringRunner to start the test as a SpringBoot application, scanning for Spring components and configurations (@RestController and @Configuration annotated classes) and using the application.yml to initialize our environment
@AutoConfigureMockMvc
//This annotation tells SpringRunner to configure the MockMvc instance that will be used to make our RESTful calls.
@ActiveProfiles("test")
// The @SpringBootTest annotation tells Spring Boot to go and look for a main configuration class (one with @SpringBootApplication for instance), and use that to start a Spring application context.
public class CetpeRestAPIMainApplicationTest {

    @Test
    public void contextLoads() throws Exception {
    }

}