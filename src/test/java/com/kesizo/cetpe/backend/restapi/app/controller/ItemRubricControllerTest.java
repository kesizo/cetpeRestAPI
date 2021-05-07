package com.kesizo.cetpe.backend.restapi.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.app.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.app.model.ItemRubric;
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
public class ItemRubricControllerTest {


    private static final String LEARNING_PROCESS_TEST_ID = "1"; //As indicated in the jdbcTemplate
    private static String usernameStudent1 = "usernameStudent1";
    private static final int ASSESSMENT_RUBRIC_ID_TEST = 1;
    private static final String ASSESSMENT_RUBRIC_TYPE_ID_TEST = "1";

    private static final int ITEM_WEIGHT_TEST = 10;


    private static final String BASE_URL = "/api/cetpe/item";
    private static final String BASE_URL_RUBRIC_ID = "/api/cetpe/rubric";
    private static final String BASE_RUBRICS_BY_LPROCESS_URL = "/api/cetpe/rubrics/by/lprocess/"+LEARNING_PROCESS_TEST_ID;

    @Autowired
    private MockMvc mvc; //MockMvc allows us to exercise our @RestController class without starting a server

    @Autowired
    private JdbcTemplate jdbcTemplate; // This allows us to interact with the test database in order to get ir ready for the tests

    @Autowired
    private ObjectMapper mapper; // Used for converting Objects to/from JSON

    @Before
    public void initTests() {

        // Always start from known state
        jdbcTemplate.execute( "DELETE FROM item_rubric;" +
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
                "INSERT INTO learning_student(username, first_name, last_name) VALUES ('usernameStudent2', 'studentName2', 'studentName2')");

        jdbcTemplate.execute("INSERT INTO learning_supervisor(username, first_name, last_name) VALUES ('user', 'supervisorName', 'supervisorLastName')");

        jdbcTemplate.execute("INSERT INTO learning_process(id,description, end_date_time, is_cal1_available, is_cal2_available, is_cal3_available, is_calf_available, limit_cal1, limit_cal2, limit_rev1, limit_rev2, " +
                "name, starting_date_time, weight_param_a, weight_param_b, weight_param_c, weight_param_d, weight_param_e, status_id, supervisor_id)\n" +
                " VALUES ("+LEARNING_PROCESS_TEST_ID+",'description', current_timestamp, false, false, false, false, 0, 0, 0, 0, 'test', current_timestamp, 20, 20, 20, 20, 20, 1, 'user');");

        jdbcTemplate.execute("INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (1, true, current_timestamp, 5, current_timestamp, 'Rubric 1', "+LEARNING_PROCESS_TEST_ID+", "+ASSESSMENT_RUBRIC_TYPE_ID_TEST+");\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (2, true, current_timestamp, 5, current_timestamp, 'Rubric 2',"+LEARNING_PROCESS_TEST_ID+", 2);\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (3, true, current_timestamp, 5, current_timestamp, 'Rubric 3',"+LEARNING_PROCESS_TEST_ID+", 3);\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (4, true, current_timestamp, 5, current_timestamp, 'Rubric 4',"+LEARNING_PROCESS_TEST_ID+", 4);\n");
    }

    @After
    public void tearDown() {

        jdbcTemplate.execute( "DELETE FROM item_rubric;" +
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
     * Should be none items in any rubric by default
     *
     * @throws Exception
     */
    @Test
    public void shouldStartWithNoneItems() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL)
                .param("id_rubric", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        //Convert JSON Result to object
        ItemRubric[] itemRubricList = this.mapper.readValue(result.getResponse().getContentAsString(), ItemRubric[].class);
        Assert.assertTrue(itemRubricList.length == 0);

        // Rubric 2
        result = mvc.perform(get(BASE_URL)
                .param("id_rubric", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        itemRubricList = this.mapper.readValue(result.getResponse().getContentAsString(), ItemRubric[].class);
        Assert.assertTrue(itemRubricList.length == 0);


        // Rubric 3
        result = mvc.perform(get(BASE_URL)
                .param("id_rubric", "3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        itemRubricList = this.mapper.readValue(result.getResponse().getContentAsString(), ItemRubric[].class);
        Assert.assertTrue(itemRubricList.length == 0);



        // Rubric 4
        result = mvc.perform(get(BASE_URL)
                 .param("id_rubric", "4")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        itemRubricList = this.mapper.readValue(result.getResponse().getContentAsString(), ItemRubric[].class);
        Assert.assertTrue(itemRubricList.length == 0);
    }

    /**
     * * Should return not found when asking to get an item with a bad id
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFoundItemByBadID() throws Exception {

        // Get: Operation
        MvcResult result = mvc.perform(get(BASE_URL+"/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldNotFoundItemByBadURL() throws Exception {

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
    public void shouldReturnNullIfItemByNonExistingID() throws Exception {

        String sNonExistingId = String.valueOf(0);

        // Get: Operation
        MvcResult resultDelete = mvc.perform(get(BASE_URL+"/"+sNonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    /**
     * Should create an item on Rubric 1 from lprocess with id=1
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldCreateItem() throws Exception {

        String itemRubricDescription = "Test Item 1 Rubric 1";

        ItemRubric itemRubricToCreate = new ItemRubric();
        itemRubricToCreate.setDescription(itemRubricDescription);
        itemRubricToCreate.setWeight(ITEM_WEIGHT_TEST);
        itemRubricToCreate.setAssessmentRubric(getRubricFromTestLearningProcess(LEARNING_PROCESS_TEST_ID));
  //      itemRubricToCreate.setItemRatesByStudent(null);

        //Creating item JSON
        byte[] itemRubricToCreateJSON = this.mapper.writeValueAsString(itemRubricToCreate).getBytes();


        // CREATE THE ITEM
        MvcResult resultInsert = mvc.perform(post(BASE_URL) // It doesn't jump to break point and I don't know why
                .content(itemRubricToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.description", is(itemRubricDescription)))
                .andExpect(jsonPath("$.weight",is(ITEM_WEIGHT_TEST)))
                .andReturn();

        ItemRubric itemInserted = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRubric.class);

        // CHECK Just inserted ITEM
        MvcResult resultsCheck = mvc.perform(get(BASE_URL+"/"+itemInserted.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andReturn();

        // RETRIEVE THE LIST OF ITEMS FROM THE RUBRIC AND CHECK THERE IS ONE
        MvcResult resultsInRubricCheck = mvc.perform(get(BASE_URL)
                .param("id_rubric", LEARNING_PROCESS_TEST_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();


    }

    /**
     * Should create an item on Rubric 1 from lprocess with id=1
     *
     * @throws Exception
     */
    @Test
    public void shouldForbiddenWhenCreateItemRegularUser() throws Exception {

        String itemRubricDescription = "Test Item 1 Rubric 1";

        ItemRubric itemRubricToCreate = new ItemRubric();
        itemRubricToCreate.setDescription(itemRubricDescription);
        itemRubricToCreate.setWeight(ITEM_WEIGHT_TEST);
        itemRubricToCreate.setAssessmentRubric(getRubricFromTestLearningProcess(LEARNING_PROCESS_TEST_ID));
        //      itemRubricToCreate.setItemRatesByStudent(null);

        //Creating item JSON
        byte[] itemRubricToCreateJSON = this.mapper.writeValueAsString(itemRubricToCreate).getBytes();


        // CREATE THE ITEM
        MvcResult resultInsert = mvc.perform(post(BASE_URL) // It doesn't jump to break point and I don't know why
                .content(itemRubricToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldBadRequestCreateItemNullDescription() throws Exception {

        // Creating Item object using test values
        String itemDescription = null;
        int itemWeight = ITEM_WEIGHT_TEST;
        long assessmentRubric_id = ASSESSMENT_RUBRIC_ID_TEST;

        ItemRubric itemObject = new ItemRubric();
        itemObject.setWeight(itemWeight);
        itemObject.setDescription(itemDescription);

        itemObject.setAssessmentRubric(getRubricFromTestLearningProcess(ASSESSMENT_RUBRIC_TYPE_ID_TEST));

        // Creating process JSON
        byte[] itemJSON = this.mapper.writeValueAsString(itemObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL).content(itemJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldBadRequestCreateItemWeightZero() throws Exception {

        // Creating Item object using test values
        String itemDescription = "Description Test";
        int itemWeight = 0;
        long assessmentRubric_id = ASSESSMENT_RUBRIC_ID_TEST;

        ItemRubric itemObject = new ItemRubric();

        itemObject.setWeight(itemWeight);
        itemObject.setDescription(itemDescription);

        itemObject.setAssessmentRubric(getRubricFromTestLearningProcess(ASSESSMENT_RUBRIC_TYPE_ID_TEST));

        // Creating process JSON
        byte[] itemJSON = this.mapper.writeValueAsString(itemObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL).content(itemJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldBadRequestCreateItemWithAssessmentRubricNull() throws Exception {

        // Creating Item object using test values
        String itemDescription = "Description Test";
        int itemWeight = ITEM_WEIGHT_TEST;


        ItemRubric itemObject = new ItemRubric();
        itemObject.setDescription(itemDescription);
        itemObject.setWeight(itemWeight);
        itemObject.setAssessmentRubric(null);

        // Creating process JSON
        byte[] itemJSON = this.mapper.writeValueAsString(itemObject).getBytes();

        // invoke Create
        MvcResult results = mvc.perform(post(BASE_URL)
                .content(itemJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Should create an item on Rubric 1 from lprocess with id=1
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldUpdateItem() throws Exception {

        String itemRubricDescription = "Test Item 1 Rubric 1";
        ItemRubric itemRubricToCreate = new ItemRubric();
        itemRubricToCreate.setDescription(itemRubricDescription);
        itemRubricToCreate.setWeight(ITEM_WEIGHT_TEST);
        itemRubricToCreate.setAssessmentRubric(getRubricFromTestLearningProcess(LEARNING_PROCESS_TEST_ID));
        //      itemRubricToCreate.setItemRatesByStudent(null);

        //Creating item JSON
        byte[] itemRubricToCreateJSON = this.mapper.writeValueAsString(itemRubricToCreate).getBytes();


        // CREATE THE ITEM
        MvcResult resultInsert = mvc.perform(post(BASE_URL) // It doesn't jump to break point and I don't know why
                .content(itemRubricToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.description", is(itemRubricDescription)))
                .andExpect(jsonPath("$.weight",is(ITEM_WEIGHT_TEST)))
                .andReturn();

        ItemRubric itemToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRubric.class);
        itemToUpdate.setDescription(itemRubricDescription + "UPDATED");
        itemToUpdate.setWeight(ITEM_WEIGHT_TEST + 5);

        byte[] itemRubricToUpdateJSON = this.mapper.writeValueAsString(itemToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+itemToUpdate.getId())
                .content(itemRubricToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.description", is(itemRubricDescription + "UPDATED")))
                .andExpect(jsonPath("$.weight",is(ITEM_WEIGHT_TEST + 5)))
                .andReturn();

    }

    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldBadRequestUpdateItemNullDescription() throws Exception {

        String itemRubricDescription = "Test Item 1 Rubric 1";
        ItemRubric itemRubricToCreate = new ItemRubric();
        itemRubricToCreate.setDescription(itemRubricDescription);
        itemRubricToCreate.setWeight(ITEM_WEIGHT_TEST);
        itemRubricToCreate.setAssessmentRubric(getRubricFromTestLearningProcess(LEARNING_PROCESS_TEST_ID));
        //      itemRubricToCreate.setItemRatesByStudent(null);

        //Creating item JSON
        byte[] itemRubricToCreateJSON = this.mapper.writeValueAsString(itemRubricToCreate).getBytes();


        // CREATE THE ITEM
        MvcResult resultInsert = mvc.perform(post(BASE_URL) // It doesn't jump to break point and I don't know why
                .content(itemRubricToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.description", is(itemRubricDescription)))
                .andExpect(jsonPath("$.weight",is(ITEM_WEIGHT_TEST)))
                .andReturn();

        ItemRubric itemToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRubric.class);
        itemToUpdate.setDescription(null);
        itemToUpdate.setWeight(ITEM_WEIGHT_TEST + 5);


        byte[] itemRubricToUpdateJSON = this.mapper.writeValueAsString(itemToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+itemToUpdate.getId())
                .content(itemRubricToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldBadRequestUpdateItemNullRubric() throws Exception {

        String itemRubricDescription = "Test Item 1 Rubric 1";
        ItemRubric itemRubricToCreate = new ItemRubric();
        itemRubricToCreate.setDescription(itemRubricDescription);
        itemRubricToCreate.setWeight(ITEM_WEIGHT_TEST);
        itemRubricToCreate.setAssessmentRubric(getRubricFromTestLearningProcess(LEARNING_PROCESS_TEST_ID));
        //      itemRubricToCreate.setItemRatesByStudent(null);

        //Creating item JSON
        byte[] itemRubricToCreateJSON = this.mapper.writeValueAsString(itemRubricToCreate).getBytes();


        // CREATE THE ITEM
        MvcResult resultInsert = mvc.perform(post(BASE_URL) // It doesn't jump to break point and I don't know why
                .content(itemRubricToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.description", is(itemRubricDescription)))
                .andExpect(jsonPath("$.weight",is(ITEM_WEIGHT_TEST)))
                .andReturn();

        ItemRubric itemToUpdate = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRubric.class);
        itemToUpdate.setAssessmentRubric(null);

        byte[] itemRubricToUpdateJSON = this.mapper.writeValueAsString(itemToUpdate).getBytes();

        // UPDATE: Operation
        MvcResult resultUpdate = mvc.perform(put(BASE_URL+"/"+itemToUpdate.getId())
                .content(itemRubricToUpdateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * Delete an existing Item will return the deleted Item
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldDeleteItem() throws Exception {

        String itemRubricDescription = "Test Item 1 Rubric 1";

        ItemRubric itemRubricToCreate = new ItemRubric();
        itemRubricToCreate.setDescription(itemRubricDescription);
        itemRubricToCreate.setWeight(ITEM_WEIGHT_TEST);
        itemRubricToCreate.setAssessmentRubric(getRubricFromTestLearningProcess(LEARNING_PROCESS_TEST_ID));
        //      itemRubricToCreate.setItemRatesByStudent(null);

        //Creating item JSON
        byte[] itemRubricToCreateJSON = this.mapper.writeValueAsString(itemRubricToCreate).getBytes();


        // CREATE THE ITEM
        MvcResult resultInsert = mvc.perform(post(BASE_URL) // It doesn't jump to break point and I don't know why
                .content(itemRubricToCreateJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.description", is(itemRubricDescription)))
                .andExpect(jsonPath("$.weight",is(ITEM_WEIGHT_TEST)))
                .andReturn();

        ItemRubric itemInserted = this.mapper.readValue(resultInsert.getResponse().getContentAsByteArray(), ItemRubric.class);

        // Check that one item is found in the database
        mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();


        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"/"+itemInserted.getId())
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
     * * Should return not found when asking to delete an item with a bad id
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldNotFoundDeleteItemByBadID() throws Exception {

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldNotFoundItemProcessByBadURL() throws Exception {

        // Delete: Operation
        MvcResult resultDelete = mvc.perform(delete(BASE_URL+"xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

    }


    /**
     *
     * Should get a 200 and false when deleting an Item by id is not found.
     *
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor_admin",roles={"PM","ADMIN"})
    public void shouldReturnFalseDeleteItemByNonExistingID() throws Exception {

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
     * Auxiliary method
     *
     * @return
     */


    private AssessmentRubric getRubricFromTestLearningProcess(String sTypeId) throws Exception {

        AssessmentRubric rubricToReturn = null;

        MvcResult result = mvc.perform(get(BASE_RUBRICS_BY_LPROCESS_URL)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andReturn();

            //Convert JSON Result to object
            AssessmentRubric[] assessmentList = this.mapper.readValue(result.getResponse().getContentAsString(), AssessmentRubric[].class);

            int typeRubricId =  Integer.parseInt(sTypeId);
            typeRubricId--;
            rubricToReturn = assessmentList[typeRubricId];
            rubricToReturn.setItemList(null);

        return rubricToReturn;
    }

}


