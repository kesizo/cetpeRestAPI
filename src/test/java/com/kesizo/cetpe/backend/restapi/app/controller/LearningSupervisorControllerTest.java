package com.kesizo.cetpe.backend.restapi.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.app.model.LearningSupervisor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
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
@WithMockUser(username="user",roles={"USER","PM"})
public class LearningSupervisorControllerTest {


    private static final String BASE_URL = "/api/cetpe/lsupervisor";

    @Autowired
    private MockMvc mvc; //MockMvc allows us to exercise our @RestController class without starting a server

    @Autowired
    private JdbcTemplate jdbcTemplate; // This allows us to interact with the test database in order to get ir ready for the tests

    @Autowired
    private ObjectMapper mapper; // Used for converting Objects to/from JSON

    @Before
    public void initTests() {

        // Always start from known state
        jdbcTemplate.execute("DELETE FROM learning_supervisor;");
    }

    @After
    public void tearDown() {

        jdbcTemplate.execute("DELETE FROM learning_supervisor;");
    }


    @Test
    public void contextLoads() throws Exception {
        assertThat(mvc).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    @Test
    public void shouldStartWithNoneSupervisor() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        //Convert JSON Result to object
        LearningSupervisor[] supervisorList = this.mapper.readValue(result.getResponse().getContentAsString(), LearningSupervisor[].class);
    }


    @Test
    public void shouldNotFoundGetSupervisorByBadURL() throws Exception {
        mvc.perform(get(BASE_URL + "xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundGetSupervisorByNonExistingUsername() throws Exception {

        mvc.perform(get(BASE_URL + "/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldCreateSupervisor() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername("username1");
        supervisorObject.setFirstName("firstname1");
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorSON)
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
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldCreateSupervisorWithUsernameLengthEquals3() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername("abc");
        supervisorObject.setFirstName("firstname1");
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorSON)
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
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldCreateSupervisorWhenUsernameLengthEquals256() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername(IntStream.range(0,256)
                .mapToObj(i->"a")
                .collect(Collectors.joining("")));
        supervisorObject.setFirstName("firstname1");
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorJSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorJSON)
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
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldBadRequestCreateSupervisorWhenUsernameIsNull() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername(null);
        supervisorObject.setFirstName("firstname1");
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorJSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldBadRequestCreateSupervisorWhenFirstNameLessThan3() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername("username1");
        supervisorObject.setFirstName("");
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorJSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldBadRequestCreateSupervisorWhenFirstNameBiggerThan256() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername("username1");
        supervisorObject.setFirstName(IntStream.range(0,257)
                .mapToObj(i->"a")
                .collect(Collectors.joining("")));
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorJSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldBadRequestCreateSupervisorWhenUsernameLengthIsLessThan3() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername("ab");
        supervisorObject.setFirstName("firstname1");
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorJSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldBadRequestCreateSupervisorWhenUsernameLengthIsBiggerThan256() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername(IntStream.range(0,257)
                .mapToObj(i->"a")
                .collect(Collectors.joining("")));
        supervisorObject.setFirstName("firstname1");
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorJSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }


    @Test
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldBadRequestCreateSupervisorWhenFirstNameIsNull() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername("username1");
        supervisorObject.setFirstName(null);
        supervisorObject.setLastName("lastname1");

        // Creating process JSON
        byte[] supervisorSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldBadRequestCreateSupervisorWhenLastNameIsNull() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername("username1");
        supervisorObject.setFirstName("firstname1");
        supervisorObject.setLastName(null);

        // Creating process JSON
        byte[] supervisorSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    @WithMockUser(username="user",roles={"ADMIN"})
    public void shouldBadRequestCreateSupervisorWhenLastNameIsEmpty() throws Exception {

        // Creating Supervisor object using test values
        LearningSupervisor supervisorObject = new LearningSupervisor();
        supervisorObject.setUsername("username1");
        supervisorObject.setFirstName("firstname1");
        supervisorObject.setLastName("");

        // Creating process JSON
        byte[] supervisorJSON = this.mapper.writeValueAsString(supervisorObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(supervisorJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

}
