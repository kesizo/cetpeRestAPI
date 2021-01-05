package com.kesizo.cetpe.backend.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.model.RubricType;
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

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
@WithMockUser(username="user",roles={"USER"})
public class AssessmentRubricControllerTest {


    private static final String BASE_URL = "/api/cetpe/rubric";
    private static final String BASE_URL_WITH_SUFFIX = "/api/cetpe/rubrics/by/lprocess";

    @Autowired
    private MockMvc mvc; //MockMvc allows us to exercise our @RestController class without starting a server

    @Autowired
    private JdbcTemplate jdbcTemplate; // This allows us to interact with the test database in order to get ir ready for the tests

    @Autowired
    private ObjectMapper mapper; // Used for converting Objects to/from JSON

    private String sLearningProcessTestId = "1"; //As indicated in the jdbcTemplate
    private String sRubricTypeTestId = "1"; //As indicated in the jdbcTemplate

    @Before
    public void initTests() {

        // Always start from known state
        jdbcTemplate.execute("DELETE FROM assessment_rubric;" +
                "DELETE FROM user_group;" +
                "DELETE FROM learning_process;" +
                "DELETE FROM learning_supervisor;" +
                "DELETE FROM learning_process_status;" +
                "DELETE FROM rubric_type;" +
                "DELETE FROM learning_student;");

        jdbcTemplate.execute(
                "INSERT INTO learning_process_status(id,name,description) values (0,'CEPTE Process Created',' Initial status for a peer evaluation process');\n" +
                        "INSERT INTO learning_process_status(id,name,description) values (1,'CEPTE Process Available','The Learning process can be accessed by students');\n" +
                        "INSERT INTO learning_process_status(id,name,description) values (2,'CEPTE Process Finished','The Learning process is finished');\n" +
                        "INSERT INTO learning_process_status(id,name,description) values (3,'CEPTE Process results available','The Learning process is finished and results are published');");

        jdbcTemplate.execute("INSERT INTO rubric_type(id,type) values ("+sRubricTypeTestId+",'Assessment of Contents');\n" +
                "INSERT INTO rubric_type(id,type) values (2,'Assessment of Assessments');\n" +
                "INSERT INTO rubric_type(id,type) values (3,'Group based Assessment');\n" +
                "INSERT INTO rubric_type(id,type) values (4,'Group members Assessment');");

        jdbcTemplate.execute("INSERT INTO learning_student(username, first_name, last_name) VALUES ('usernameStudent1', 'studentName1', 'studentName1');\n" +
                "INSERT INTO learning_student(username, first_name, last_name) VALUES ('usernameStudent2', 'studentName2', 'studentName2')");

        jdbcTemplate.execute("INSERT INTO learning_supervisor(username, first_name, last_name) VALUES ('user', 'supervisorName', 'supervisorLastName')");

        jdbcTemplate.execute("INSERT INTO learning_process(id,description, end_date_time, is_cal1_available, is_cal2_available, is_cal3_available, is_calf_available, limit_cal1, limit_cal2, limit_rev1, limit_rev2, " +
                "name, starting_date_time, weight_param_a, weight_param_b, weight_param_c, weight_param_d, weight_param_e, status_id, supervisor_id)\n" +
                " VALUES ("+sLearningProcessTestId+",'description', current_timestamp, false, false, false, false, 0, 0, 0, 0, 'test', current_timestamp, 20, 20, 20, 20, 20, 1, 'user');");


    }

    @After
    public void tearDown() {
        // Always start from known state
        jdbcTemplate.execute("DELETE FROM assessment_rubric;" +
                "DELETE FROM user_group;" +
                "DELETE FROM learning_process;" +
                "DELETE FROM learning_supervisor;" +
                "DELETE FROM learning_process_status;" +
                "DELETE FROM rubric_type;" +
                "DELETE FROM learning_student;");
    }


    @Test
    public void contextLoads() throws Exception {
        assertThat(mvc).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    /**
     * Should be none rubrics by default
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldStartWithNoneRubrics() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        //Convert JSON Result to object
        AssessmentRubric[] assessmentRubrics = this.mapper
                                                .readValue(result.getResponse().getContentAsString(), AssessmentRubric[].class);
    }


    @Test
    public void shouldNotFoundGetRubricByBadURL() throws Exception {
        mvc.perform(get(BASE_URL + "xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldBadRequestGetRubricByBadID() throws Exception {
        mvc.perform(get(BASE_URL + "/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotFoundGetRubricByNonExistingID() throws Exception {

        mvc.perform(get(BASE_URL + "/25")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Create a new AssessmentRubric will return the created AssessmentRubric
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldCreateAssessmentRubric() throws Exception {

        // Creating Rubric object using test values
        String rubricTitle = "test_rubric_title";
        int rubricRank = 4;
        boolean rubricIsEnabled = true;

        AssessmentRubric rubricObject = new AssessmentRubric();
        rubricObject.setTitle(rubricTitle);
        rubricObject.setStarting_date_time(LocalDateTime.now());
        rubricObject.setEnd_date_time(LocalDateTime.now());
        rubricObject.setEnabled(rubricIsEnabled);
        rubricObject.setRank(rubricRank);

        rubricObject.setRubricType(getMockTestRubricType());
        rubricObject.setLearningProcess(getMockTestLearningProcess());

        // Creating process JSON
        byte[] rubricJSON = this.mapper.writeValueAsString(rubricObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL).content(rubricJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(rubricTitle)))
                .andExpect(jsonPath("$.rank", is(rubricRank)))
                .andExpect(jsonPath("$.enabled", is(rubricIsEnabled)))
                .andReturn();
    }

    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldCreateAssessmentRubricWithEmptyItemList() throws Exception {

        // Creating Rubric object using test values
        String rubricTitle = "test_rubric_title";
        int rubricRank = 4;
        boolean rubricIsEnabled = true;

        AssessmentRubric rubricObject = new AssessmentRubric();
        rubricObject.setTitle(rubricTitle);
        rubricObject.setStarting_date_time(LocalDateTime.now());
        rubricObject.setEnd_date_time(LocalDateTime.now());
        rubricObject.setEnabled(rubricIsEnabled);
        rubricObject.setRank(rubricRank);
        rubricObject.setItemList(new ArrayList<>());
        rubricObject.setRubricType(getMockTestRubricType());
        rubricObject.setLearningProcess(getMockTestLearningProcess());

        // Creating process JSON
        byte[] rubricJSON = this.mapper.writeValueAsString(rubricObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL).content(rubricJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(rubricTitle)))
                .andExpect(jsonPath("$.rank", is(rubricRank)))
                .andExpect(jsonPath("$.enabled", is(rubricIsEnabled)))
                .andReturn();
    }

    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldCreateAssessmentRubricWithNullItemList() throws Exception {

        // Creating Rubric object using test values
        String rubricTitle = "test_rubric_title";
        int rubricRank = 4;
        boolean rubricIsEnabled = true;

        AssessmentRubric rubricObject = new AssessmentRubric();
        rubricObject.setTitle(rubricTitle);
        rubricObject.setStarting_date_time(LocalDateTime.now());
        rubricObject.setEnd_date_time(LocalDateTime.now());
        rubricObject.setEnabled(rubricIsEnabled);
        rubricObject.setRank(rubricRank);
        rubricObject.setItemList(null);
        rubricObject.setRubricType(getMockTestRubricType());
        rubricObject.setLearningProcess(getMockTestLearningProcess());

        // Creating process JSON
        byte[] rubricJSON = this.mapper.writeValueAsString(rubricObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL).content(rubricJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(rubricTitle)))
                .andExpect(jsonPath("$.rank", is(rubricRank)))
                .andExpect(jsonPath("$.enabled", is(rubricIsEnabled)))
                .andReturn();
    }

    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldBadRequestCreateAssessmentRubricNullTitle() throws Exception {

        // Creating Rubric object using test values
        String rubricTitle = null;
        int rubricRank = 4;
        boolean rubricIsEnabled = true;

        AssessmentRubric rubricObject = new AssessmentRubric();
        rubricObject.setTitle(rubricTitle);
        rubricObject.setStarting_date_time(LocalDateTime.now());
        rubricObject.setEnd_date_time(LocalDateTime.now());
        rubricObject.setEnabled(rubricIsEnabled);
        rubricObject.setRank(rubricRank);

        rubricObject.setRubricType(getMockTestRubricType());
        rubricObject.setLearningProcess(getMockTestLearningProcess());

        // Creating process JSON
        byte[] rubricJSON = this.mapper.writeValueAsString(rubricObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL).content(rubricJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldBadRequestCreateAssessmentRubricEmptyTitle() throws Exception {

        // Creating Rubric object using test values
        String rubricTitle = "";
        int rubricRank = 4;
        boolean rubricIsEnabled = true;

        AssessmentRubric rubricObject = new AssessmentRubric();
        rubricObject.setTitle(rubricTitle);
        rubricObject.setStarting_date_time(LocalDateTime.now());
        rubricObject.setEnd_date_time(LocalDateTime.now());
        rubricObject.setEnabled(rubricIsEnabled);
        rubricObject.setRank(rubricRank);

        rubricObject.setRubricType(getMockTestRubricType());
        rubricObject.setLearningProcess(getMockTestLearningProcess());

        // Creating process JSON
        byte[] rubricJSON = this.mapper.writeValueAsString(rubricObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL).content(rubricJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    /**
     * Update an existing Rubric  will return the updated AssessmentRubric
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldUpdateAssessmentRubric() throws Exception {

        // Creating Rubric object using test values
        String rubricTitle = "test_rubric_title";
        int rubricRank = 4;
        boolean rubricIsEnabled = true;

        AssessmentRubric rubricObject = new AssessmentRubric();
        rubricObject.setTitle(rubricTitle);
        rubricObject.setStarting_date_time(LocalDateTime.now());
        rubricObject.setEnd_date_time(LocalDateTime.now());
        rubricObject.setEnabled(rubricIsEnabled);
        rubricObject.setRank(rubricRank);

        rubricObject.setRubricType(getMockTestRubricType());
        rubricObject.setLearningProcess(getMockTestLearningProcess());

        // Creating process JSON
        byte[] rubricJSON = this.mapper.writeValueAsString(rubricObject).getBytes();

        // invoke Create
        MvcResult resultInsert = mvc.perform(post(BASE_URL).content(rubricJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(rubricTitle)))
                .andExpect(jsonPath("$.rank", is(rubricRank)))
                .andExpect(jsonPath("$.enabled", is(rubricIsEnabled)))
                .andReturn();


        AssessmentRubric rubricUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), AssessmentRubric.class);
        // Update the inserted AssessmentRubric
        rubricUpdatable.setTitle(rubricTitle +" updated");
        rubricUpdatable.setRank(rubricRank +1 );
        rubricUpdatable.setEnabled(!rubricIsEnabled);

        rubricJSON = this.mapper.writeValueAsString(rubricUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+rubricUpdatable.getId())
                .content(rubricJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.title", is(rubricTitle +" updated")))
                .andExpect(jsonPath("$.rank", is(rubricRank + 1)))
                .andExpect(jsonPath("$.enabled", is(!rubricIsEnabled)))
                .andReturn();


    }

    /**
     * Should return the rubrics associated to the LearningProcessId passed as parameter in the url
     *
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldRetrieveRubricsAssociatedToLearningProcessId() throws Exception {

        // Creating Rubric object using test values
        String rubricTitle = "test_rubric_title";
        int rubricRank = 4;
        boolean rubricIsEnabled = true;

        AssessmentRubric rubricObject = new AssessmentRubric();
        rubricObject.setTitle(rubricTitle);
        rubricObject.setStarting_date_time(LocalDateTime.now());
        rubricObject.setEnd_date_time(LocalDateTime.now());
        rubricObject.setEnabled(rubricIsEnabled);
        rubricObject.setRank(rubricRank);

        rubricObject.setRubricType(getMockTestRubricType());
        rubricObject.setLearningProcess(getMockTestLearningProcess());

        // Creating process JSON
        byte[] rubricJSON = this.mapper.writeValueAsString(rubricObject).getBytes();

        // invoke Create
        MvcResult resultInsert = mvc.perform(post(BASE_URL).content(rubricJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(rubricTitle)))
                .andExpect(jsonPath("$.rank", is(rubricRank)))
                .andExpect(jsonPath("$.enabled", is(rubricIsEnabled)))
                .andReturn();

        // GET: Operation Check
        MvcResult result = mvc.perform(get(BASE_URL_WITH_SUFFIX+"/"+getMockTestLearningProcess().getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    /**
     * Auxiliary method
     *
     * @return
     */
    private LearningProcess getMockTestLearningProcess() {


        LearningProcess testLearningProcess = new LearningProcess();
        testLearningProcess.setId(Long.parseLong(sLearningProcessTestId));
        testLearningProcess.setName("test");
        testLearningProcess.setDescription("description");
        testLearningProcess.setStarting_date_time(LocalDateTime.now());
        testLearningProcess.setEnd_date_time(LocalDateTime.now());

        LearningSupervisor learningSupervisor =  new LearningSupervisor();
        learningSupervisor.setFirstName("supervisorName");
        learningSupervisor.setLastName("supervisorLastName");
        learningSupervisor.setUsername("user");

        testLearningProcess.setLearning_supervisor(learningSupervisor);

        return testLearningProcess;
    }

    /**
     * Auxiliary method
     *
     * @return
     */
    private RubricType getMockTestRubricType() {


        RubricType testRubricType = new RubricType();
        testRubricType.setId(Long.parseLong(sRubricTypeTestId));
        testRubricType.setType("Assessment of Contents");

        return testRubricType;
    }

}
