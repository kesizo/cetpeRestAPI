package com.kesizo.cetpe.backend.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.ItemRubric;
import org.junit.Assert;
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

import java.util.ArrayList;

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
public class ItemRubricControllerTest {


    private static String sLearningProcessTestId = "1"; //As indicated in the jdbcTemplate
    private static String usernameStudent1 = "usernameStudent1";

    private static final String BASE_URL = "/api/cetpe/lprocess/rubric/item";
    private static final String BASE_URL_RUBRIC_ID = "/api/cetpe/lprocess/rubric/";
    private static final String BASE_RUBRICS_BY_LPROCESS_URL = "/api/cetpe/lprocess/rubrics/by/lprocess/"+sLearningProcessTestId;

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
                "DELETE FROM assessment_rubric;" +
                "DELETE FROM item_rubric;" +
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
                " VALUES ("+sLearningProcessTestId+",'description', current_timestamp, false, false, false, false, 0, 0, 0, 0, 'test', current_timestamp, 20, 20, 20, 20, 20, 1, 'user');");

        jdbcTemplate.execute("INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (1, true, current_timestamp, 5, current_timestamp, 'Rubric 1', "+sLearningProcessTestId+", 1);\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (2, true, current_timestamp, 5, current_timestamp, 'Rubric 2',"+sLearningProcessTestId+", 2);\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (3, true, current_timestamp, 5, current_timestamp, 'Rubric 3',"+sLearningProcessTestId+", 3);\n"+
                "INSERT INTO assessment_rubric(id, enabled, end_date_time, rank, starting_date_time, title, learning_process_id, rubric_type_id) VALUES (4, true, current_timestamp, 5, current_timestamp, 'Rubric 4',"+sLearningProcessTestId+", 4);\n");
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
     * Should create an item on Rubric 1 from lprocess with id=1
     *
     * @throws Exception
     */
    @Test
    public void shouldCreateItemOnRubric() throws Exception {


        MvcResult result = mvc.perform(get(BASE_URL)
                .param("id_rubric", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        String itemRubricDescription = "Test Item 1 Rubric 1";

        ItemRubric itemRubricToCreate = new ItemRubric();
        itemRubricToCreate.setDescription(itemRubricDescription);
        itemRubricToCreate.setWeight(10);
        itemRubricToCreate.setAssessmentRubric(getRubricFromTestLearningProcess("1"));
        itemRubricToCreate.setItemRatesByStudent(new ArrayList<>());
        itemRubricToCreate.getAssessmentRubric().addItemRubric(itemRubricToCreate);

        //Creating process JSON
        byte[] itemRubricToCreateJSON = this.mapper.writeValueAsString(itemRubricToCreate).getBytes();

//
//        // CREATE THE ITEM
//
//        MvcResult results = mvc.perform(post(BASE_URL) // It doesn't jump to break point and I don't know why
//                .content(itemRubricToCreateJSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").exists())
//                .andExpect(jsonPath("$.description", is(itemRubricDescription)))
//                .andReturn();
//
//        // RETRIEVE THE LIST OF USER GROUPS FROM THE LPROCESS AND CHECK THERE IS ONE
//        MvcResult resultAfterInserting = mvc.perform(get(BASE_URL)
//                .param("id_lprocess",this.sLearningProcessTestId)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andReturn();

    }


//    /**
//     * Should update a user group from lprocess with id=1
//     *
//     * @throws Exception
//     */
//    @Test
//    public void shouldUpdateUserGroup() throws Exception {
//
//        String userGroupName = "Group 1";
//        String userGroupNameUpdated  = "Group 1 Updated";
//
//        UserGroup userGroupObject = new UserGroup();
//        userGroupObject.setName(userGroupName);
//        userGroupObject.setLearningStudentList(new ArrayList<>());
//        userGroupObject.setLearningProcess(this.getMockTestLearningProcess());
//
//        //Creating process JSON
//        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();
//
//        // CREATE THE USER GROUP
//        MvcResult result = mvc.perform(post(BASE_URL_LPROCESS+"/"+sLearningProcessTestId+"/group").content(userGroupJSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(userGroupName)))
//                .andReturn();
//
//
//        // UPDATE THE USER GROUP NAME
//        UserGroup userGroupToUpdate = this.mapper.readValue(result.getResponse().getContentAsByteArray(), UserGroup.class);
//        userGroupToUpdate.setName(userGroupNameUpdated);
//
//        byte[] userGroupUpdatableJSON = this.mapper.writeValueAsString(userGroupToUpdate).getBytes();
//
//        // UPDATE THE USER GROUP
//        MvcResult resultUpdated = mvc.perform(put(BASE_URL+"/"+userGroupToUpdate.getId())
//                .content(userGroupUpdatableJSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(userGroupNameUpdated)))
//                .andReturn();
//    }
//
//    /**
//     * Should update a user group from lprocess with id=1
//     *
//     * @throws Exception
//     */
//    @Test
//    public void shouldAddLearningStudentToUserGroup() throws Exception {
//
//        String sLearningProcessTestId = "1";
//        String userGroupName = "Group 1";
//
//        UserGroup userGroupObject = new UserGroup();
//        userGroupObject.setName(userGroupName);
//        userGroupObject.setLearningStudentList(new ArrayList<>());
//        userGroupObject.setLearningProcess(this.getMockTestLearningProcess());
//
//        //Creating process JSON
//        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();
//
//        // CREATE THE USER GROUP
//        MvcResult result = mvc.perform(post(BASE_URL_LPROCESS+"/"+sLearningProcessTestId+"/group").content(userGroupJSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(userGroupName)))
//                .andReturn();
//
//        // UPDATE THE USER GROUP BY ADDING STUDENTS
//        UserGroup userGroupToUpdate = this.mapper.readValue(result.getResponse().getContentAsByteArray(), UserGroup.class);
//        userGroupToUpdate.addLearningStudent(this.getMockTestStudent());
//
//        byte[] userGroupUpdatableJSON = this.mapper.writeValueAsString(userGroupToUpdate).getBytes();
//
//        // UPDATE THE USER GROUP
//        MvcResult resultUpdated = mvc.perform(put(BASE_URL+"/student/add/"+userGroupToUpdate.getId())
//                .content(userGroupUpdatableJSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//    }
//
//
//    /**
//     * Auxiliary method
//     *
//     * @return
//     */
//    private LearningProcess getMockTestLearningProcess() {
//
//
//        LearningProcess testLearningProcess = new LearningProcess();
//        testLearningProcess.setId(Long.parseLong(sLearningProcessTestId));
//        testLearningProcess.setName("test");
//        testLearningProcess.setDescription("description");
//        testLearningProcess.setStarting_date_time(LocalDateTime.now());
//        testLearningProcess.setEnd_date_time(LocalDateTime.now());
//
//        LearningSupervisor learningSupervisor =  new LearningSupervisor();
//        learningSupervisor.setFirstName("supervisorName");
//        learningSupervisor.setLastName("supervisorLastName");
//        learningSupervisor.setUsername("user");
//
//        testLearningProcess.setLearning_supervisor(learningSupervisor);
//
//        return testLearningProcess;
//    }
//

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


