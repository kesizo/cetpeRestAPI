package com.kesizo.cetpe.backend.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.model.RubricType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class RubricTypeControllerTest {

    private static final String BASE_URL = "/api/cetpe/rubric/types";

    @Autowired
    private MockMvc mvc; //MockMvc allows us to exercise our @RestController class without starting a server

    @Autowired
    private JdbcTemplate jdbcTemplate; // This allows us to interact with the test database in order to get ir ready for the tests

    @Autowired
    private ObjectMapper mapper; // Used for converting Objects to/from JSON


    @Before
    public void initTests() {

        // Always start from known state
        jdbcTemplate.execute("DELETE FROM assessment_rubric;" +
                "DELETE FROM rel_user_group_learning_student;" +
                "DELETE FROM user_group;" +
                "DELETE FROM learning_process;" +
                "DELETE FROM learning_supervisor;" +
                "DELETE FROM learning_process_status;" +
                "DELETE FROM rubric_type;" +
                "DELETE FROM learning_student;");


        jdbcTemplate.execute("INSERT INTO rubric_type(id,type) values (1,'Assessment of Contents');\n"+
                "INSERT INTO rubric_type(id,type) values (2,'Assessment of Assessments');\n" +
                "INSERT INTO rubric_type(id,type) values (3,'Group based Assessment');\n" +
                "INSERT INTO rubric_type(id,type) values (4,'Group members Assessment');");
        }


    @Test
    public void contextLoads() throws Exception {
        assertThat(mvc).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    /**
     * Should be 4 rubric types
     *
     * @throws Exception
     */
    @Test
    public void shouldStartWith4RubricTypes() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andReturn();

        //Convert JSON Result to object
        RubricType[] rubricTypeList = this.mapper.readValue(result.getResponse().getContentAsString(), RubricType[].class);
    }

    /**
     * Should retrieve Rubric type objects with ids 1,2,3 and 4
     *
     * @throws Exception
     */
    @Test
    public void shouldCheckRubricTypesWithIds() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL+"/"+1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.type", is("Assessment of Contents")))
                .andReturn();

        result = mvc.perform(get(BASE_URL+"/"+2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.type", is("Assessment of Assessments")))
                .andReturn();

        result = mvc.perform(get(BASE_URL+"/"+3)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.type", is("Group based Assessment")))
                .andReturn();

        result = mvc.perform(get(BASE_URL+"/"+4)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.type", is("Group members Assessment")))
                .andReturn();

    }

    /**
     * Should get an 404 error when get a Rubric Type bad url.
     *
     * @throws Exception
     */
    @Test
    public void shouldReturnNothingGetRubricTypeByBadURL() throws Exception {
        mvc.perform(get(BASE_URL + "xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Should get an internal server error when get a Rubric Type by non-numeric id.
     *
     * @throws Exception
     */
    @Test
    public void shouldReturnNothingGetRubricTypeByBadId() throws Exception {
        mvc.perform(get(BASE_URL + "/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Should get an internal server error when get a Rubric Type by a numeric id different from 1,2,3 or 4.
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundGetRubricTypeByInvalidId() throws Exception {
        mvc.perform(get(BASE_URL + "/5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
