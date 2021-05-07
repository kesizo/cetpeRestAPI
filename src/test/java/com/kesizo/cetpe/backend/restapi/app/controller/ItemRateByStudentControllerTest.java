package com.kesizo.cetpe.backend.restapi.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.app.model.*;
import org.junit.After;
import org.junit.Assert;
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
public class ItemRateByStudentControllerTest {


    private static final String LEARNING_PROCESS_TEST_ID = "1"; //As indicated in the jdbcTemplate
    private static String usernameStudent1 = "usernameStudent1";
    private static final String usernameStudent2 = "usernameStudent2";
    private static final int ASSESSMENT_RUBRIC_ID_TEST = 1;
    private static final String ASSESSMENT_RUBRIC_TYPE_ID_TEST = "1";
    private static final int ASSESSMENT_RUBRIC_RANK_TEST = 5;

    private static final String ITEM_TEST_ID = "1"; //As indicated in the jdbcTemplate

    private static final int RATE_TEST = 5;
    private static final String RATE_JUSTIFICATION = "Test Justification";




    private static final String BASE_URL = "/api/cetpe/rate";
    private static final String ITEM_BASE_URL = "/api/cetpe/item";
    private static final String LPROCESS_BASE_URL = "/api/cetpe/lprocess";
    private static final String GROUP_BASE_URL = "/api/cetpe/group";


    @Autowired
    private MockMvc mvc; //MockMvc allows us to exercise our @RestController class without starting a server

    @Autowired
    private JdbcTemplate jdbcTemplate; // This allows us to interact with the test database in order to get ir ready for the tests

    @Autowired
    private ObjectMapper mapper; // Used for converting Objects to/from JSON

    @Before
    public void initTests() {

        // Always start from known state
        jdbcTemplate.execute( "DELETE FROM item_rate_by_student;" +
                "DELETE FROM item_rubric;" +
                "DELETE FROM assessment_rubric;" +
                "DELETE FROM rel_user_group_learning_student;" +
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

        jdbcTemplate.execute("INSERT INTO learning_student(username, first_name, last_name) VALUES ('"+usernameStudent1+"', 'studentName1', 'studentName1');\n" +
                "INSERT INTO learning_student(username, first_name, last_name) VALUES ('"+usernameStudent2+"', 'studentName2', 'studentName2')");

        jdbcTemplate.execute("INSERT INTO learning_supervisor(username, first_name, last_name) VALUES ('user', 'supervisorName', 'supervisorLastName')");

        jdbcTemplate.execute("INSERT INTO learning_process(id,description, end_date_time, is_cal1_available, is_cal2_available, is_cal3_available, is_calf_available, limit_cal1, limit_cal2, limit_rev1, limit_rev2, " +
                "name, starting_date_time, weight_param_a, weight_param_b, weight_param_c, weight_param_d, weight_param_e, status_id, supervisor_id)\n" +
                " VALUES ("+LEARNING_PROCESS_TEST_ID+",'description', current_timestamp, false, false, false, false, 0, 0, 0, 0, 'test', current_timestamp, 20, 20, 20, 20, 20, 1, 'user');");

        jdbcTemplate.execute("INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (1, true, current_timestamp,"+ASSESSMENT_RUBRIC_RANK_TEST+", current_timestamp, 'Rubric 1', "+LEARNING_PROCESS_TEST_ID+", "+ASSESSMENT_RUBRIC_TYPE_ID_TEST+");\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (2, true, current_timestamp, "+ASSESSMENT_RUBRIC_RANK_TEST+", current_timestamp, 'Rubric 2',"+LEARNING_PROCESS_TEST_ID+", 2);\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (3, true, current_timestamp, "+ASSESSMENT_RUBRIC_RANK_TEST+", current_timestamp, 'Rubric 3',"+LEARNING_PROCESS_TEST_ID+", 3);\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (4, true, current_timestamp, "+ASSESSMENT_RUBRIC_RANK_TEST+", current_timestamp, 'Rubric 4',"+LEARNING_PROCESS_TEST_ID+", 4);\n");

        jdbcTemplate.execute("INSERT INTO item_rubric(id, description, weight, assessment_rubric_id) VALUES ("+ITEM_TEST_ID+", 'Item 1 Rubric 1 description', 25, "+ASSESSMENT_RUBRIC_ID_TEST+" )");

    }

    @After
    public void tearDown() {
        // Always start from known state
        jdbcTemplate.execute( "DELETE FROM item_rate_by_student;" +
                "DELETE FROM item_rubric;" +
                "DELETE FROM assessment_rubric;" +
                "DELETE FROM rel_user_group_learning_student;" +
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
     * Should be no rates by default
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="admin",roles={"ADMIN"})
    public void shouldStartWithNoRates() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL)
                //.param("id_rubric", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        //Convert JSON Result to object
        ItemRateByStudent[] ratesList = this.mapper.readValue(result.getResponse().getContentAsString(), ItemRateByStudent[].class);
        Assert.assertTrue(ratesList.length == 0);
    }

    /**
     * * Should return not found when asking to get a rate with a bad id
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundRateByBadID() throws Exception {

        // Get: Operation
        MvcResult result = mvc.perform(get(BASE_URL+"/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldNotFoundRateByBadURL() throws Exception {

        // Get: Operation
        MvcResult result = mvc.perform(get(BASE_URL+"xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }

    /**
     *
     * Should get not found.
     *
     *
     * @throws Exception
     */
    @Test
    public void shouldReturnNullIfRateByNonExistingID() throws Exception {

        String sNonExistingId = String.valueOf(0);

        // Get: Operation
        MvcResult resultDelete = mvc.perform(get(BASE_URL+"/"+sNonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldNotFoundRatesByBadItemID() throws Exception {

        // Get: Operation
        MvcResult result = mvc.perform(get(BASE_URL+"s/item"+"/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldNotFoundRatesByBadItemURL() throws Exception {

        // Get: Operation
        MvcResult result = mvc.perform(get(BASE_URL+"s/item"+"xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }

    @Test
    public void shouldReturnEmptyListIfRatesByNonExistingItemID() throws Exception {

        String sNonExistingItemId = String.valueOf(0);

        // Get: Operation
        MvcResult resultDelete = mvc.perform(get(BASE_URL+"s/item"+"/"+sNonExistingItemId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();
    }

    /**
     * Should create an ItemRateByStudent on RubricId=1 Item=1 and from lprocess with id=1
     *
     *
     * @throws Exception
     */
    @Test
    public void shouldCreateRateStudentTarget() throws Exception {

        ItemRateByStudent rateToCreate = new ItemRateByStudent();
        rateToCreate.setRate(RATE_TEST);
        rateToCreate.setJustification(RATE_JUSTIFICATION);
        rateToCreate.setLearningStudent(this.getMockTestStudent());
        rateToCreate.setTargetStudent(this.getMockTestTargetStudent());
        rateToCreate.setTargetUserGroup(null);
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToCreate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToCreateJSON = this.mapper.writeValueAsString(rateToCreate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateInserted = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);

        // CHECK Just inserted ITEM
        MvcResult resultsCheck = mvc.perform(get(BASE_URL+"/"+rateInserted.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andReturn();

    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldCreateRateUserGroupTarget() throws Exception {

        ItemRateByStudent rateToCreate = new ItemRateByStudent();
        rateToCreate.setRate(RATE_TEST);
        rateToCreate.setJustification(RATE_JUSTIFICATION);
        rateToCreate.setLearningStudent(this.getMockTestStudent());
        rateToCreate.setTargetStudent(null);
        rateToCreate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToCreate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToCreateJSON = this.mapper.writeValueAsString(rateToCreate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateInserted = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);

        // CHECK Just inserted ITEM
        MvcResult resultsCheck = mvc.perform(get(BASE_URL+"/"+rateInserted.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andReturn();

    }

    @Test
    public void shouldBadRequestCreateRateWhenBothTargetsAreNull() throws Exception {

        ItemRateByStudent rateToCreate = new ItemRateByStudent();
        rateToCreate.setRate(RATE_TEST);
        rateToCreate.setJustification(RATE_JUSTIFICATION);
        rateToCreate.setLearningStudent(this.getMockTestStudent());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToCreate.setItemRubric(itemParent);
        rateToCreate.setTargetStudent(null);
        rateToCreate.setTargetUserGroup(null);


        //Creating item JSON
        byte[] rateToCreateJSON = this.mapper.writeValueAsString(rateToCreate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldBadRequestCreateRateWhenNoneTargetsAreNull() throws Exception {

        ItemRateByStudent rateToCreate = new ItemRateByStudent();
        rateToCreate.setRate(RATE_TEST);
        rateToCreate.setJustification(RATE_JUSTIFICATION);
        rateToCreate.setLearningStudent(this.getMockTestStudent());
        rateToCreate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        rateToCreate.setTargetStudent(this.getMockTestTargetStudent());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToCreate.setItemRubric(itemParent);
        rateToCreate.setTargetStudent(null);
        rateToCreate.setTargetUserGroup(null);


        //Creating item JSON
        byte[] rateToCreateJSON = this.mapper.writeValueAsString(rateToCreate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldBadRequestCreateRateNullJustification() throws Exception {


        ItemRateByStudent rateToCreate = new ItemRateByStudent();
        rateToCreate.setRate(RATE_TEST);
        rateToCreate.setJustification(null);
        rateToCreate.setLearningStudent(this.getMockTestStudent());

        rateToCreate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToCreate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToCreateJSON = this.mapper.writeValueAsString(rateToCreate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();



    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // because it need it for the mock
    public void shouldBadRequestCreateRateNegativeRate() throws Exception {


        ItemRateByStudent rateToCreate = new ItemRateByStudent();
        rateToCreate.setRate(-1);
        rateToCreate.setJustification(RATE_JUSTIFICATION);
        rateToCreate.setLearningStudent(this.getMockTestStudent());

        rateToCreate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToCreate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToCreateJSON = this.mapper.writeValueAsString(rateToCreate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();


    }

    @Test
    public void shouldBadRequestCreateRateHigherThanRubricRank() throws Exception {


        ItemRateByStudent rateToCreate = new ItemRateByStudent();
        rateToCreate.setRate(ASSESSMENT_RUBRIC_RANK_TEST+1);
        rateToCreate.setJustification(RATE_JUSTIFICATION);
        rateToCreate.setLearningStudent(this.getMockTestStudent());
        rateToCreate.setTargetStudent(this.getMockTestTargetStudent());

        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToCreate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToCreateJSON = this.mapper.writeValueAsString(rateToCreate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();


    }


    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldUpdateRateJustificationAndRate() throws Exception {


        ItemRateByStudent rateToUpdate = new ItemRateByStudent();
        rateToUpdate.setRate(RATE_TEST);
        rateToUpdate.setJustification(RATE_JUSTIFICATION);
        rateToUpdate.setLearningStudent(this.getMockTestStudent());
        rateToUpdate.setTargetStudent(null);
        rateToUpdate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToUpdate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToUpdateJSON = this.mapper.writeValueAsString(rateToUpdate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateCreatedToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);
        rateCreatedToUpdate.setJustification(RATE_JUSTIFICATION + "UPDATED");
        rateCreatedToUpdate.setRate(RATE_TEST - 1);

        byte[] itemRateToUpdateJSON = this.mapper.writeValueAsString(rateCreatedToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+rateCreatedToUpdate.getId())
                .content(itemRateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST -1)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION + "UPDATED")))
                .andReturn();

    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldBadRequestUpdateRateJustificationNull() throws Exception {


        ItemRateByStudent rateToUpdate = new ItemRateByStudent();
        rateToUpdate.setRate(RATE_TEST);
        rateToUpdate.setJustification(RATE_JUSTIFICATION);
        rateToUpdate.setLearningStudent(this.getMockTestStudent());
        rateToUpdate.setTargetStudent(null);
        rateToUpdate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToUpdate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToUpdateJSON = this.mapper.writeValueAsString(rateToUpdate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateCreatedToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);
        rateCreatedToUpdate.setJustification(null);
        rateCreatedToUpdate.setRate(RATE_TEST - 1);

        byte[] itemRateToUpdateJSON = this.mapper.writeValueAsString(rateCreatedToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+rateCreatedToUpdate.getId())
                .content(itemRateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldBadRequestUpdateRateWithHigherThanRubricRank() throws Exception {


        ItemRateByStudent rateToUpdate = new ItemRateByStudent();
        rateToUpdate.setRate(ASSESSMENT_RUBRIC_RANK_TEST);
        rateToUpdate.setJustification(RATE_JUSTIFICATION);
        rateToUpdate.setLearningStudent(this.getMockTestStudent());
        rateToUpdate.setTargetStudent(null);
        rateToUpdate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToUpdate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToUpdateJSON = this.mapper.writeValueAsString(rateToUpdate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateCreatedToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);
        rateCreatedToUpdate.setJustification(RATE_JUSTIFICATION + "UPDATED");
        rateCreatedToUpdate.setRate(ASSESSMENT_RUBRIC_RANK_TEST + 1);

        byte[] itemRateToUpdateJSON = this.mapper.writeValueAsString(rateCreatedToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+rateCreatedToUpdate.getId())
                .content(itemRateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldBadRequestUpdateRateWithNegativeRate() throws Exception {


        ItemRateByStudent rateToUpdate = new ItemRateByStudent();
        rateToUpdate.setRate(RATE_TEST);
        rateToUpdate.setJustification(RATE_JUSTIFICATION);
        rateToUpdate.setLearningStudent(this.getMockTestStudent());
        rateToUpdate.setTargetStudent(null);
        rateToUpdate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToUpdate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToUpdateJSON = this.mapper.writeValueAsString(rateToUpdate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateCreatedToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);
        rateCreatedToUpdate.setJustification(RATE_JUSTIFICATION + "UPDATED");
        rateCreatedToUpdate.setRate(-1);

        byte[] itemRateToUpdateJSON = this.mapper.writeValueAsString(rateCreatedToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+rateCreatedToUpdate.getId())
                .content(itemRateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"}) // because this.getMockTestTargetUserGroup needs to be USER and ADMIN to create the user group
    public void shouldBadRequestUpdateRateBothTargetsAreNull() throws Exception {

        ItemRateByStudent rateToUpdate = new ItemRateByStudent();
        rateToUpdate.setRate(RATE_TEST);
        rateToUpdate.setJustification(RATE_JUSTIFICATION);
        rateToUpdate.setLearningStudent(this.getMockTestStudent());
        rateToUpdate.setTargetStudent(null);
        rateToUpdate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToUpdate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToUpdateJSON = this.mapper.writeValueAsString(rateToUpdate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateCreatedToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);
        rateCreatedToUpdate.setTargetStudent(null);
        rateCreatedToUpdate.setTargetUserGroup(null);

        byte[] itemRateToUpdateJSON = this.mapper.writeValueAsString(rateCreatedToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+rateCreatedToUpdate.getId())
                .content(itemRateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldBadRequestUpdateRateBothTargetsAreNotNull() throws Exception {


        ItemRateByStudent rateToUpdate = new ItemRateByStudent();
        rateToUpdate.setRate(RATE_TEST);
        rateToUpdate.setJustification(RATE_JUSTIFICATION);
        rateToUpdate.setLearningStudent(this.getMockTestStudent());
        rateToUpdate.setTargetStudent(null);
        rateToUpdate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToUpdate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToUpdateJSON = this.mapper.writeValueAsString(rateToUpdate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateCreatedToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);
        rateCreatedToUpdate.setTargetStudent(this.getMockTestTargetStudent());
        rateCreatedToUpdate.setTargetUserGroup(this.getMockTestTargetUserGroup());

        byte[] itemRateToUpdateJSON = this.mapper.writeValueAsString(rateCreatedToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+rateCreatedToUpdate.getId())
                .content(itemRateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }


    /**
     * Delete an existing Rate will return the deleted Rate
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="admin",roles={"ADMIN"}) // Needed to support the getMock Methods
    public void shouldDeleteRate() throws Exception {

        ItemRateByStudent rateToUpdate = new ItemRateByStudent();
        rateToUpdate.setRate(RATE_TEST);
        rateToUpdate.setJustification(RATE_JUSTIFICATION);
        rateToUpdate.setLearningStudent(this.getMockTestStudent());
        rateToUpdate.setTargetStudent(null);
        rateToUpdate.setTargetUserGroup(this.getMockTestTargetUserGroup());
        ItemRubric itemParent = getMockExistingTestItemHelper();
        rateToUpdate.setItemRubric(itemParent);

        //Creating item JSON
        byte[] rateToUpdateJSON = this.mapper.writeValueAsString(rateToUpdate).getBytes();

        // CREATE THE Rate
        MvcResult resultInsert = mvc.perform(post(BASE_URL)
                .content(rateToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.rate", is(RATE_TEST)))
                .andExpect(jsonPath("$.justification",is(RATE_JUSTIFICATION)))
                .andReturn();

        ItemRateByStudent rateCreatedToDelete = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRateByStudent.class);

        // Check that one item is found in the database
        mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"/"+rateCreatedToDelete.getId())
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
     * * Should return not found when asking to delete an rate with a bad id
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundDeleteRateByBadID() throws Exception {

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldNotFoundDeleteRateByBadURL() throws Exception {

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }


    /**
     *
     * Should get a 200 and false when deleting an Rate by id is not found.
     *
     *
     * @throws Exception
     */
    @Test
    public void shouldReturnFalseDeleteRateByNonExistingID() throws Exception {

        String sNonExistingId = String.valueOf(0);

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"/"+sNonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$",is(false)))
                .andReturn();
    }


    /* ----------------------------------------------------------
     * ------------------ HELPER METHODS ------------------------
     * ----------------------------------------------------------
     */

    private ItemRubric getMockExistingTestItemHelper() throws Exception {
        //Retrieve the existing item which the rate will belong to
        MvcResult resultedItem = mvc.perform(get(ITEM_BASE_URL+"/"+ITEM_TEST_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andReturn();

        return this.mapper.readValue(resultedItem.getResponse().getContentAsByteArray(), ItemRubric.class);
    }

    private LearningProcess getMockExistingTestLearningProcessHelper() {

        try {
            //Retrieve the existing item which the rate will belong to
            MvcResult resultedLProcess = mvc.perform(get(LPROCESS_BASE_URL + "/" + 1)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").exists())
                    .andReturn();

            return this.mapper.readValue(resultedLProcess.getResponse().getContentAsByteArray(), LearningProcess.class);
        }
        catch (Exception e) {
            return  null;
        }
    }

    private LearningStudent getMockTestStudent() {


        LearningStudent testStudent = new LearningStudent();

        testStudent.setFirstName("studentName1");
        testStudent.setLastName("studentName1");
        testStudent.setUsername(usernameStudent1);

        return testStudent;
    }

    private LearningStudent getMockTestTargetStudent() {


        LearningStudent testStudent = new LearningStudent();

        testStudent.setFirstName("studentName2");
        testStudent.setLastName("studentName2");
        testStudent.setUsername(usernameStudent2);

        return testStudent;
    }

    @WithMockUser
    private UserGroup getMockTestTargetUserGroup() throws Exception {



        String userGroupName = "Group 1";

        UserGroup userGroupObject = new UserGroup();
        userGroupObject.setName(userGroupName);
        userGroupObject.setLearningStudentList(new ArrayList<>());
        userGroupObject.setLearningProcess(this.getMockExistingTestLearningProcessHelper());

        //Creating process JSON
        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();

        // CREATE THE USER GROUP
        MvcResult result = mvc.perform(post(LPROCESS_BASE_URL+"/"+LEARNING_PROCESS_TEST_ID+"/group")
                .content(userGroupJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(userGroupName)))
                .andReturn();

        // UPDATE THE USER GROUP BY ADDING STUDENTS
        UserGroup userGroupToUpdate = this.mapper.readValue(result.getResponse().getContentAsByteArray(), UserGroup.class);
        userGroupToUpdate.addLearningStudent(this.getMockTestStudent());

        byte[] userGroupUpdatableJSON = this.mapper.writeValueAsString(userGroupToUpdate).getBytes();

        // UPDATE THE USER GROUP
        MvcResult resultUpdated = mvc.perform(put(GROUP_BASE_URL+"/student/add/"+userGroupToUpdate.getId())
                .content(userGroupUpdatableJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserGroup testTargetUserGroup = this.mapper.readValue(result.getResponse().getContentAsByteArray(), UserGroup.class);


        return testTargetUserGroup;
    }

}


