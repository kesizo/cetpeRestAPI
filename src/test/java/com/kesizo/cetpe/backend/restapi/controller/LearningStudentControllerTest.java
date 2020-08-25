package com.kesizo.cetpe.backend.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SAMPLE, TUTORIALS AND INFO CAN BE FOUND HERE:
 *  1) https://github.com/finsterthecat/heroes-backend/blob/master/src/test/java/io/navan/heroesbackend/HeroesBackendApplicationTests.java
 *  2) https://hellokoding.com/restful-apis-example-with-spring-boot-integration-test-with-mockmvc-ui-integration-with-vuejs/
 *  3) https://www.blazemeter.com/blog/spring-boot-rest-api-unit-testing-with-junit/
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//This annotation tells SpringRunner to start the test as a SpringBoot application, scanning for Spring components and configurations (@RestController and @Configuration annotated classes) and using the application.yml to initialize our environment
@AutoConfigureMockMvc
//This annotation tells SpringRunner to configure the MockMvc instance that will be used to make our RESTful calls.
@ActiveProfiles("test")
public class LearningStudentControllerTest {


    private static final String BASE_URL = "/api/cetpe/lstudent";

    @Autowired
    private MockMvc mvc; //MockMvc allows us to exercise our @RestController class without starting a server

    @Autowired
    private JdbcTemplate jdbcTemplate; // This allows us to interact with the test database in order to get ir ready for the tests

    @Autowired
    private ObjectMapper mapper; // Used for converting Objects to/from JSON

    @Before
    public void initTests() {

        // Always start from known state
        jdbcTemplate.execute("DELETE FROM learning_student;");
    }

    @After
    public void tearDown() {

        jdbcTemplate.execute("DELETE FROM learning_student;");
    }


    @Test
    public void contextLoads() throws Exception {
        assertThat(mvc).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    @Test
    public void shouldStartWithNoneStudents() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        //Convert JSON Result to object
        LearningStudent[] studentList = this.mapper.readValue(result.getResponse().getContentAsString(), LearningStudent[].class);
    }


    @Test
    public void shouldNotFoundGetStudentByBadURL() throws Exception {
        mvc.perform(get(BASE_URL + "xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundGetStudentByNonExistingUsername() throws Exception {

        mvc.perform(get(BASE_URL + "/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateStudent() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername("username1");
        studentObject.setFirstName("firstname1");
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.username", is("username1")))
                .andExpect(jsonPath("$.firstName", is("firstname1")))
                .andExpect(jsonPath("$.lastName", is("lastname1")))
                .andReturn();

        MvcResult result = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    public void shouldCreateStudentWithUsernameLengthEquals3() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername("abc");
        studentObject.setFirstName("firstname1");
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.username", is("abc")))
                .andExpect(jsonPath("$.firstName", is("firstname1")))
                .andExpect(jsonPath("$.lastName", is("lastname1")))
                .andReturn();

        MvcResult result = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    public void shouldCreateStudentWhenUsernameLengthEquals256() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername(IntStream.range(0,256)
                .mapToObj(i->"a")
                .collect(Collectors.joining("")));
        studentObject.setFirstName("firstname1");
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentJSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.firstName", is("firstname1")))
                .andExpect(jsonPath("$.lastName", is("lastname1")))
                .andReturn();

        MvcResult result = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    public void shouldBadRequestCreateStudentWhenUsernameIsNull() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername(null);
        studentObject.setFirstName("firstname1");
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentJSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldBadRequestCreateStudentWhenFirstNameLessThan3() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername("username1");
        studentObject.setFirstName("");
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentJSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldBadRequestCreateStudentWhenFirstNameBiggerThan256() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername("username1");
        studentObject.setFirstName(IntStream.range(0,257)
                                            .mapToObj(i->"a")
                                            .collect(Collectors.joining("")));
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentJSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldBadRequestCreateStudentWhenUsernameLengthIsLessThan3() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername("ab");
        studentObject.setFirstName("firstname1");
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentJSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldBadRequestCreateStudentWhenUsernameLengthIsBiggerThan256() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername(IntStream.range(0,257)
                                            .mapToObj(i->"a")
                                            .collect(Collectors.joining("")));
        studentObject.setFirstName("firstname1");
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentJSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }


    @Test
    public void shouldBadRequestCreateStudentWhenFirstNameIsNull() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername("username1");
        studentObject.setFirstName(null);
        studentObject.setLastName("lastname1");

        // Creating process JSON
        byte[] studentSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldBadRequestCreateStudentWhenLastNameIsNull() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername("username1");
        studentObject.setFirstName("firstname1");
        studentObject.setLastName(null);

        // Creating process JSON
        byte[] studentJSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldBadRequestCreateStudentWhenLastNameIsEmpty() throws Exception {

        // Creating Student object using test values
        LearningStudent studentObject = new LearningStudent();
        studentObject.setUsername("username1");
        studentObject.setFirstName("firstname1");
        studentObject.setLastName("");

        // Creating process JSON
        byte[] studentJSON = this.mapper.writeValueAsString(studentObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(studentJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

}
