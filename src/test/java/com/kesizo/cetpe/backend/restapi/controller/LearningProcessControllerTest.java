package com.kesizo.cetpe.backend.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.model.UserGroup;
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

import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
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
public class LearningProcessControllerTest {


    private static final String BASE_URL = "/api/cetpe/lprocess";

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

        jdbcTemplate.execute("INSERT INTO rubric_type(id,type) values (1,'Assessment of Contents');\n"+
                "INSERT INTO rubric_type(id,type) values (2,'Assessment of Assessments');\n" +
                "INSERT INTO rubric_type(id,type) values (3,'Group based Assessment');\n" +
                "INSERT INTO rubric_type(id,type) values (4,'Group members Assessment');");

        jdbcTemplate.execute("INSERT INTO learning_student(username, first_name, last_name) VALUES ('usernameStudent1', 'studentName1', 'studentName1');\n" +
                "INSERT INTO learning_student(username, first_name, last_name) VALUES ('usernameStudent2', 'studentName2', 'studentName2')");

        jdbcTemplate.execute("INSERT INTO learning_supervisor(username, first_name, last_name) VALUES ('user', 'supervisorName', 'supervisorLastName')");
    }


    @Test
    public void contextLoads() throws Exception {
        assertThat(mvc).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    /**
     * Should be none learning processes by default
     *
     * @throws Exception
     */
    @Test
    public void shouldStartWithNoneLearningProcess() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        //Convert JSON Result to object
        LearningProcess[] learningProcesses = this.mapper.readValue(result.getResponse().getContentAsString(), LearningProcess[].class);
    }

    /**
     * Should get an internal server error when get a Learning process by a wrong url
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundGetLearningProcessByBadURL() throws Exception {
        mvc.perform(get(BASE_URL + "xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Should get an internal server error when get a Learning process by non-numeric id.
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundGetLearningProcessByBadID() throws Exception {
        mvc.perform(get(BASE_URL + "/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Should get a HTTP Status 404 when Get Learning Process by id is not found.
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundGetLearningProcessByNonExistingID() throws Exception {


        // Retrieve the list of all processes...
        MvcResult result = mvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        LearningProcess[] users = this.mapper.readValue(result.getResponse().getContentAsString(), LearningProcess[].class);

        // Search the process with the highest id...
        LearningProcess learningProcessHighestId = Arrays.asList(users)
                .stream()
                .max((Comparator.comparing(LearningProcess::getId)))
                .orElse(null);

        String sNonExistingId = String.valueOf(0);

        if (learningProcessHighestId!=null) {
            sNonExistingId = String.valueOf(learningProcessHighestId.getId()+1);
        }

        // Search by id with a non-existing id by adding 1 to the highest id
        mvc.perform(get(BASE_URL + "/"+sNonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Create a new Learning Process will return the created LearningProcess
     *
     * @throws Exception
     */
    @Test
    public void shouldCreateLearningProcess() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        // Creating process JSON
        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // invoke Create Learning Process
        MvcResult results = mvc.perform(post(BASE_URL).content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();

        LearningProcess learningProcess = this.mapper.readValue(results.getResponse().getContentAsByteArray(), LearningProcess.class);

        // Now we check via get operation if the insert has been successfully performed
        // InvokeGetLearningProcess
        MvcResult result = mvc.perform(get(BASE_URL+"/"+learningProcess.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name",is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andExpect(jsonPath("$.learning_process_status.id", is(0)))
                .andReturn();
    }

    /**
     * Fail when creating a new Learning Process with null title
     *
     * @throws Exception
     */
    //@Test(expected = org.springframework.web.util.NestedServletException.class) // No needed because it's handled in the controller
    @Test
    public void shouldBadRequestCreateLearningProcessNullTitle() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = null;
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        //Creating process JSON
        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // invoke Create Learning Process
        MvcResult results = mvc.perform(post(BASE_URL).content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Fail when creating a new Learning Process with null description
     *
     * @throws Exception
     */
    //@Test(expected = org.springframework.web.util.NestedServletException.class)
    @Test
    public void shouldBadRequestCreateLearningProcessNullDescription() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = null;
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        //Creating process JSON
        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // invoke Create Learning Process
        MvcResult results = mvc.perform(post(BASE_URL).content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Fail when creating a new Learning Process with null supervisor
     *
     * @throws Exception
     */
    //@Test(expected = org.springframework.web.util.NestedServletException.class)
    @Test
    public void shouldBadRequestCreateLearningProcessNullSupervisor() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = null;


        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(null);

        //Creating process JSON
        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // invoke Create Learning Process
        MvcResult results = mvc.perform(post(BASE_URL).content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Update an existing Learning Process will return the updated LearningProcess
     *
     * @throws Exception
     */
    @Test
    public void shouldUpdateLearningProcess() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // INSERT: Operation
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();


        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);
        // Update the inserted learning process
        learningProcessUpdatable.setName(learningProcessTitle +" updated");
        learningProcessUpdatable.setDescription(learningProcessDescription +" updated");
        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name",is(learningProcessTitle + " updated")))
                .andExpect(jsonPath("$.description", is(learningProcessDescription + " updated")))
                .andReturn();

        // GET: Operation Check
        MvcResult result = mvc.perform(get(BASE_URL+"/"+learningProcessUpdatable.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name",is(learningProcessTitle + " updated")))
                .andExpect(jsonPath("$.description", is(learningProcessDescription + " updated")))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andExpect(jsonPath("$.learning_process_status.id", is(0)))
                .andReturn();

    }

    @Test
    public void shouldBadRequestUpdateLearningProcessNullTitle() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // INSERT: Operation
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();


        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);
        // Update the inserted learning process
        learningProcessUpdatable.setName(null);
        learningProcessUpdatable.setDescription(learningProcessDescription +" updated");
        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)
    public void shouldBadRequestUpdateLearningProcessEmptyTitle() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // INSERT: Operation
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();


        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);
        // Update the inserted learning process
        learningProcessUpdatable.setName("");
        learningProcessUpdatable.setDescription(learningProcessDescription +" updated");
        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    //@Test(expected = org.springframework.web.util.NestedServletException.class) since it's managed in the controller now it's not needed
    @Test
    public void shouldBadRequestUpdateLearningProcessNullDescription() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // INSERT: Operation
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();


        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);
        // Update the inserted learning process
        learningProcessUpdatable.setName(learningProcessTitle +" updated");
        learningProcessUpdatable.setDescription(null);
        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)
    public void shouldBadRequestUpdateLearningProcessEmptyDescription() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // INSERT: Operation
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();


        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);
        // Update the inserted learning process
        learningProcessUpdatable.setName(learningProcessTitle +" updated");
        learningProcessUpdatable.setDescription("");
        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Delete an existing Learning Process will return the deleted LearningProcess
     *
     * @throws Exception
     */
    @Test
    public void shouldDeleteLearningProcess() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // INSERT: Operation
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();

        LearningProcess learningProcessToDelete = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);

        // Check that one process is found in the database
        mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();


        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"/"+learningProcessToDelete.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$",is(true)))
                .andReturn();

        // Check that none processes are found in the database
        mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

    }


    /**
     *
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundDeleteLearningProcessByBadID() throws Exception {

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    /**
     *
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundDeleteLearningProcessByBadURL() throws Exception {

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }

    /**
     *
     * Should get a 200 and false when deleting a Learning Process by id is not found.
     *
     *
     * @throws Exception
     */
    @Test
    public void shouldReturnFalseDeleteLearningProcessByNonExistingID() throws Exception {

        String sNonExistingId = String.valueOf(0);

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"/"+sNonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$",is(false)))
                .andReturn();
    }

    /**
     * Update an existing Learning Process will return the updated LearningProcess
     *
     * @throws Exception
     */
    @Test
    public void shouldUpdateStatusLearningProcess() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // INSERT: Operation
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();

        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);
        // Update the inserted learning process
        learningProcessUpdatable.setLearning_process_status(new LearningProcessStatus(2,"test status 2", "test status 2 description"));
        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.learning_process_status.id", is(2)))
                .andExpect(jsonPath("$").exists())
                .andReturn();

        // GET: Operation Check
        MvcResult result = mvc.perform(get(BASE_URL+"/"+learningProcessUpdatable.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name",is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andExpect(jsonPath("$.learning_process_status.id", is(2)))
                .andReturn();

    }


    /**
     * Update an existing Learning Process will return bad request when status learning process is null
     *
     * @throws Exception
     */
    @Test
    public void shouldBadRequestUpdateStatusLearningProcessNullStatus() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // INSERT: Operation
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andReturn();

        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);
        // Update the inserted learning process

        learningProcessUpdatable.setLearning_process_status(null);
        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * Add user groups to an existing Learning Process will return the updated LearningProcess
     *
     * @throws Exception
     */
    @Test
    public void shouldAddUserGroupToLearningProcess() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // 1) INSERT a Learning Process without UserGroups
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andExpect(jsonPath("$.userGroupList",is(nullValue())))
                .andReturn();


        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);


        // 2) ADDING USER GROUP: Update the inserted learning process
        UserGroup userGroupNew = new UserGroup();
        userGroupNew.setName("User Group test");
        userGroupNew.setLearningProcess(learningProcessUpdatable);
        learningProcessUpdatable.addUserGroup(userGroupNew);

        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/usergroup/add"+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.userGroupList",hasSize(1)))
                .andReturn();

        // GET: Operation Check
        MvcResult result = mvc.perform(get(BASE_URL+"/"+learningProcessUpdatable.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.userGroupList",hasSize(1)))
                .andReturn();
    }

    /**
     * Remove user groups to an existing Learning Process will return the updated LearningProcess
     *
     * @throws Exception
     */
    @Test
    public void shouldRemoveUserGroupToLearningProcess() throws Exception {

        // Creating learning process object using test values
        String learningProcessTitle = "Test_title";
        String learningProcessDescription = "Test_description";
        String learningProcessSupervisorUsername = "user";

        LearningProcess learningProcessObject = new LearningProcess();
        learningProcessObject.setName(learningProcessTitle);
        learningProcessObject.setDescription(learningProcessDescription);
        learningProcessObject.setLearning_supervisor(new LearningSupervisor(learningProcessSupervisorUsername,"firstName","lastName"));

        byte[] learningProcessJSON = this.mapper.writeValueAsString(learningProcessObject).getBytes();

        // 1) INSERT a Learning Process without UserGroups
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreate
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(learningProcessTitle)))
                .andExpect(jsonPath("$.description", is(learningProcessDescription)))
                .andExpect(jsonPath("$.learning_supervisor.username", is(learningProcessSupervisorUsername)))
                .andExpect(jsonPath("$.userGroupList",is(nullValue())))
                .andReturn();


        LearningProcess learningProcessUpdatable = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), LearningProcess.class);


        // 2) ADDING USER GROUP: Update the inserted learning process
        UserGroup userGroupNew = new UserGroup();
        userGroupNew.setName("User Group test");
        userGroupNew.setLearningProcess(learningProcessUpdatable);
        learningProcessUpdatable.addUserGroup(userGroupNew);

        learningProcessJSON = this.mapper.writeValueAsString(learningProcessUpdatable).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/usergroup/add"+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.userGroupList",hasSize(1)))
                .andReturn();

        // GET: Operation Check
        MvcResult result = mvc.perform(get(BASE_URL+"/"+learningProcessUpdatable.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.userGroupList",hasSize(1)))
                .andReturn();

        LearningProcess learningProcessToRemovedUserGroup = this.mapper.readValue(result.getResponse().getContentAsByteArray(), LearningProcess.class);
        learningProcessToRemovedUserGroup.removeUserGroup(userGroupNew);

        // UPDATE: Operation
        MvcResult resultUpdateRemovedUserGroups = mvc.perform(put(BASE_URL+"/usergroup/remove"+"/"+learningProcessUpdatable.getId())
                .content(learningProcessJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //    .andExpect(jsonPath("$").exists())
                //  .andExpect(jsonPath("$.userGroupList",hasSize(0)))
                .andReturn();

    }



//
//
//    /**
//     * Create a new DelphiUser will fail with null name.
//     *
//     * @throws Exception
//     */
//
//    @Test(expected = org.springframework.web.util.NestedServletException.class)
//    public void shouldBadRequestCreateDelphiUserWithNullName() throws Exception {
//
//        // Creating delphiUser object using test values
//        String delphiTestUserNameBad = null;
//        String delphiTestUserPassword = "Test_password";
//
//        DelphiUser delphiUserObjectNullWhiteName = new DelphiUser();
//        delphiUserObjectNullWhiteName.setName(delphiTestUserNameBad);
//        delphiUserObjectNullWhiteName.setPassword(delphiTestUserPassword);
//
//        //Creating user JSON
//        byte[] delphiUserJson = this.mapper.writeValueAsString(delphiUserObjectNullWhiteName).getBytes();
//
//
//        // invokeCreateDelphiUser
//        MvcResult result = mvc.perform(post(BASE_URL).content(delphiUserJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreateDelphiUiser
//                .andExpect(status().isBadRequest())
//                .andReturn();
//    }
//
//    /**
//     * Create a new DelphiUser will fail with empty name
//     *
//     * @throws Exception
//     */
//
//    @Test(expected = org.springframework.web.util.NestedServletException.class)
//    public void shouldBadRequestCreateDelphiUserWithEmptyName() throws Exception {
//
//        // Creating delphiUser object using test values
//        String delphiTestUserNameBad = "";
//        String delphiTestUserPassword = "Test_password";
//
//        DelphiUser delphiUserObjectNullWhiteName = new DelphiUser();
//        delphiUserObjectNullWhiteName.setName(delphiTestUserNameBad);
//        delphiUserObjectNullWhiteName.setPassword(delphiTestUserPassword);
//
//
//        //Creating user JSON
//        byte[] delphiUserJson = this.mapper.writeValueAsString(delphiUserObjectNullWhiteName).getBytes();
//
//
//        // invokeCreateDelphiUser
//        MvcResult result = mvc.perform(post(BASE_URL).content(delphiUserJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreateDelphiUiser
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        MvcResult results = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("Miguel")))
//                .andExpect(jsonPath("$[1].name", is("Hilda")))
//                .andReturn();
//    }
//
//    /**
//     * Create a new DelphiUser will fail with name with less than 3 chars
//     *
//     * @throws Exception
//     */
//
//    @Test(expected = org.springframework.web.util.NestedServletException.class)
//    public void shouldBadRequestCreateDelphiUserWithNameLengthLessThan3() throws Exception {
//
//        // Creating delphiUser object using test values
//        String delphiTestUserNameBad = "12";
//        String delphiTestUserPassword = "Test_password";
//
//        DelphiUser delphiUserObjectNullWhiteName = new DelphiUser();
//        delphiUserObjectNullWhiteName.setName(delphiTestUserNameBad);
//        delphiUserObjectNullWhiteName.setPassword(delphiTestUserPassword);
//
//
//        //Creating user JSON
//        byte[] delphiUserJson = this.mapper.writeValueAsString(delphiUserObjectNullWhiteName).getBytes();
//
//
//        // invokeCreateDelphiUser
//        MvcResult result = mvc.perform(post(BASE_URL).content(delphiUserJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreateDelphiUiser
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        MvcResult results = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("Miguel")))
//                .andExpect(jsonPath("$[1].name", is("Hilda")))
//                .andReturn();
//    }

//    @Test
//    public void shouldGetDelphiUsersById() throws Exception {
//
//
//        MvcResult result = mvc.perform(get(BASE_URL+"/10").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").exists())
//                .andExpect(jsonPath("$.name", is("Miguel")))
//                .andExpect(jsonPath("$.password", is("borrascas")))
//                .andReturn();
//
//        //Convert JSON Result to object
//        //DelphiUser users = this.mapper.readValue(result.getResponse().getContentAsString(), DelphiUser.class);
//        //LOG.debug("Delphi user 0 id: {}", users[0].getId());
//        //LOG.debug("Delphi user 1 id: {}", users[1].getId());
//    }
//

//
//    @Test(expected = org.springframework.web.util.NestedServletException.class)
//    public void shouldBadRequestCreateDelphiUserWithNameLengthBiggerThan256() throws Exception {
//
//        // Creating delphiUser object using test values
//
//        //creating a string of 257 chars
//        /*
//        String delphiTestUserNameBad = IntStream.range(0,257)
//                                                .mapToObj(i->"c")
//                                                .collect(Collectors.joining(""));*/
//        String delphiTestUserNameBad = String.join("", Collections.nCopies(257, "c")); //cccc...
//
//        String delphiTestUserPassword = "Test_password";
//
//        DelphiUser delphiUserObjectNullWhiteName = new DelphiUser();
//        delphiUserObjectNullWhiteName.setName(delphiTestUserNameBad);
//        delphiUserObjectNullWhiteName.setPassword(delphiTestUserPassword);
//
//
//        //Creating user JSON
//        byte[] delphiUserJson = this.mapper.writeValueAsString(delphiUserObjectNullWhiteName).getBytes();
//
//
//        // invokeCreateDelphiUser
//        MvcResult result = mvc.perform(post(BASE_URL).content(delphiUserJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreateDelphiUiser
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        MvcResult results = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("Miguel")))
//                .andExpect(jsonPath("$[1].name", is("Hilda")))
//                .andReturn();
//
//
//    }
//
//    /**
//     * Create a new DelphiUser will fail with null name.
//     *
//     * @throws Exception
//     */
//
//    @Test(expected = org.springframework.web.util.NestedServletException.class)
//    public void shouldBadRequestCreateDelphiUserWithNullPassword() throws Exception {
//
//        // Creating delphiUser object using test values
//        String delphiTestUserName = "Test_name";
//        String delphiTestUserPasswordBad = null;
//
//        DelphiUser delphiUserObjectNullWhiteName = new DelphiUser();
//        delphiUserObjectNullWhiteName.setName(delphiTestUserName);
//        delphiUserObjectNullWhiteName.setPassword(delphiTestUserPasswordBad);
//
//        //Creating user JSON
//        byte[] delphiUserJson = this.mapper.writeValueAsString(delphiUserObjectNullWhiteName).getBytes();
//
//
//        // invokeCreateDelphiUser
//        MvcResult result = mvc.perform(post(BASE_URL).content(delphiUserJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreateDelphiUiser
//                .andExpect(status().isBadRequest())
//                .andReturn();
//    }
//
//    @Test(expected = org.springframework.web.util.NestedServletException.class)
//    public void shouldBadRequestCreateDelphiUserWithEmptyPassword() throws Exception {
//
//        // Creating delphiUser object using test values
//        String delphiTestUserName = "Test_name";
//        String delphiTestUserPasswordBad = "";
//
//        DelphiUser delphiUserObjectNullWhiteName = new DelphiUser();
//        delphiUserObjectNullWhiteName.setName(delphiTestUserName);
//        delphiUserObjectNullWhiteName.setPassword(delphiTestUserPasswordBad);
//
//
//        //Creating user JSON
//        byte[] delphiUserJson = this.mapper.writeValueAsString(delphiUserObjectNullWhiteName).getBytes();
//
//
//        // invokeCreateDelphiUser
//        MvcResult result = mvc.perform(post(BASE_URL).content(delphiUserJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreateDelphiUiser
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        MvcResult results = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("Miguel")))
//                .andExpect(jsonPath("$[1].name", is("Hilda")))
//                .andReturn();
//    }
//
//    @Test(expected = org.springframework.web.util.NestedServletException.class)
//    public void shouldBadRequestCreateDelphiUserWithPasswordLengthLessThan8() throws Exception {
//
//        // Creating delphiUser object using test values
//        String delphiTestUserName = "Test_name";
//        String delphiTestUserPasswordBad = "1234567";
//
//        DelphiUser delphiUserObjectNullWhiteName = new DelphiUser();
//        delphiUserObjectNullWhiteName.setName(delphiTestUserName);
//        delphiUserObjectNullWhiteName.setPassword(delphiTestUserPasswordBad);
//
//
//        //Creating user JSON
//        byte[] delphiUserJson = this.mapper.writeValueAsString(delphiUserObjectNullWhiteName).getBytes();
//
//
//        // invokeCreateDelphiUser
//        MvcResult result = mvc.perform(post(BASE_URL).content(delphiUserJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreateDelphiUiser
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        MvcResult results = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("Miguel")))
//                .andExpect(jsonPath("$[1].name", is("Hilda")))
//                .andReturn();
//    }
//
//    @Test(expected = org.springframework.web.util.NestedServletException.class)
//    public void shouldBadRequestCreateDelphiUserWithPasswordLengthBiggerThan256() throws Exception {
//
//        // Creating delphiUser object using test values
//        String delphiTestUserName = "Test_name";
//
//        //creating a string of 257 chars
//        String delphiTestUserPasswordBad = IntStream.range(0,257)
//                .mapToObj(i->"c")
//                .collect(Collectors.joining(""));
//
//        DelphiUser delphiUserObjectNullWhiteName = new DelphiUser();
//        delphiUserObjectNullWhiteName.setName(delphiTestUserName);
//        delphiUserObjectNullWhiteName.setPassword(delphiTestUserPasswordBad);
//
//
//        //Creating user JSON
//        byte[] delphiUserJson = this.mapper.writeValueAsString(delphiUserObjectNullWhiteName).getBytes();
//
//
//        // invokeCreateDelphiUser
//        MvcResult result = mvc.perform(post(BASE_URL).content(delphiUserJson)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)) //here ends invokeCreateDelphiUiser
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        MvcResult results = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("Miguel")))
//                .andExpect(jsonPath("$[1].name", is("Hilda")))
//                .andReturn();
//    }
////
////    <T> T fromJsonResult(MvcResult result, Class<T> tClass) throws Exception {
////        return this.mapper.readValue(result.getResponse().getContentAsString(), tClass);
////    }

}

