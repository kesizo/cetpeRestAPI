package com.kesizo.cetpe.backend.restapi.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.app.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.app.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.app.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.app.model.UserGroup;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class UserGroupControllerTest {

    private static final String BASE_URL = "/api/cetpe/group";
    private static final String BASE_URL_LPROCESS_GROUP = "/api/cetpe/lprocess/group";
    private static final String BASE_URL_LPROCESS = "/api/cetpe/lprocess";

    @Autowired
    private MockMvc mvc; //MockMvc allows us to exercise our @RestController class without starting a server

    @Autowired
    private JdbcTemplate jdbcTemplate; // This allows us to interact with the test database in order to get ir ready for the tests

    @Autowired
    private ObjectMapper mapper; // Used for converting Objects to/from JSON

    private String sLearningProcessTestId = "1"; //As indicated in the jdbcTemplate
    private String usernameStudent1 = "usernameStudent1";


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
    }

    @After
    public void tearDown() {

        jdbcTemplate.execute("DELETE FROM assessment_rubric;" +
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
     * Should be none learning processes by default
     *
     * @throws Exception
     */
    @Test
    public void shouldStartWithNoneUserGroups() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        //Convert JSON Result to object
        UserGroup[] userGroupList = this.mapper.readValue(result.getResponse().getContentAsString(), UserGroup[].class);
        assertThat(userGroupList).isEmpty();
    }

    /**
     * Should be none user groups from lprocess with id=1
     *
     * @throws Exception
     */
    @Test
    public void shouldStartWithNoneUserGroupsOnProcess() throws Exception {

        MvcResult result = mvc.perform(get(BASE_URL_LPROCESS_GROUP)
                .param("id_lprocess","1") // adding  ?id_lprocess=1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        //Convert JSON Result to object
        UserGroup[] userGroupList = this.mapper.readValue(result.getResponse().getContentAsString(), UserGroup[].class);
    }

    /**
     * Should create a user groups from lprocess with id=1
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor",roles={"PM","ADMIN"})
    public void shouldCreateUserGroup() throws Exception {


        String userGroupName = "Group 1";

        UserGroup userGroupObject = new UserGroup();
        userGroupObject.setName(userGroupName);
        userGroupObject.setLearningStudentList(new ArrayList<>());

        LearningProcess currentLearningProcess = this.getMockTestLearningProcess();
        userGroupObject.setLearningProcess(currentLearningProcess);

        //Creating process JSON
        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();

        // CREATE THE USER GROUP
        MvcResult results = mvc.perform(post(BASE_URL_LPROCESS+"/"+sLearningProcessTestId+"/group").content(userGroupJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(userGroupName)))
                .andReturn();

        // RETRIEVE THE LIST OF USER GROUPS FROM THE LPROCESS AND CHECK THERE IS ONE
        MvcResult resultAfterUpdate = mvc.perform(get(BASE_URL_LPROCESS_GROUP)
                .param("id_lprocess","1") // adding  ?id_lprocess=1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

    }


    /**
     * Should create a user groups from lprocess with id=1 and Name null
     *
     * @throws Exception
     */
   // @Test(expected = org.springframework.web.util.NestedServletException.class)
    @WithMockUser(username="supervisor",roles={"PM","ADMIN"})
    public void shouldBadRequestCreateUserGroupWithNullName() throws Exception {


        String userGroupName = null;

        UserGroup userGroupObject = new UserGroup();
        userGroupObject.setName(userGroupName);
        userGroupObject.setLearningStudentList(new ArrayList<>());

        LearningProcess currentLearningProcess = this.getMockTestLearningProcess();
        userGroupObject.setLearningProcess(currentLearningProcess);

        //Creating process JSON
        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();

        // CREATE THE USER GROUP
        MvcResult results = mvc.perform(post(BASE_URL_LPROCESS+"/"+sLearningProcessTestId+"/group").content(userGroupJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    /**
     * Should create a user groups from lprocess with id=1 and Name empty
     *
     * @throws Exception
     */
    @WithMockUser(username="supervisor",roles={"PM","ADMIN"})
    public void shouldBadRequestCreateUserGroupWithEmptyName() throws Exception {


        String userGroupName = "";

        UserGroup userGroupObject = new UserGroup();
        userGroupObject.setName(userGroupName);
        userGroupObject.setLearningStudentList(new ArrayList<>());

        LearningProcess currentLearningProcess = this.getMockTestLearningProcess();
        userGroupObject.setLearningProcess(currentLearningProcess);

        //Creating process JSON
        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();

        // CREATE THE USER GROUP
        MvcResult results = mvc.perform(post(BASE_URL_LPROCESS+"/"+sLearningProcessTestId+"/group").content(userGroupJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    /**
     * Should create a user groups from lprocess with id=1 and Name length Higher Than256
     *
     * @throws Exception
     */
    //@Test(expected = org.springframework.web.util.NestedServletException.class)
    @WithMockUser(username="supervisor",roles={"PM","ADMIN"})
    public void shouldBadRequestCreateUserGroupWithNameLengthHigherThan256() throws Exception {


        String userGroupName = Stream.generate(() -> String.valueOf("a")).limit(257).collect(Collectors.joining());

        UserGroup userGroupObject = new UserGroup();
        userGroupObject.setName(userGroupName);
        userGroupObject.setLearningStudentList(new ArrayList<>());

        LearningProcess currentLearningProcess = this.getMockTestLearningProcess();
        userGroupObject.setLearningProcess(currentLearningProcess);

        //Creating process JSON
        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();

        // CREATE THE USER GROUP
        MvcResult results = mvc.perform(post(BASE_URL_LPROCESS+"/"+sLearningProcessTestId+"/group").content(userGroupJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }



    /**
     * Should update a user group from lprocess with id=1
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor",roles={"PM","ADMIN"})
    public void shouldUpdateUserGroup() throws Exception {

        String userGroupName = "Group 1";
        String userGroupNameUpdated  = "Group 1 Updated";

        UserGroup userGroupObject = new UserGroup();
        userGroupObject.setName(userGroupName);
        userGroupObject.setLearningStudentList(new ArrayList<>());
        userGroupObject.setLearningProcess(this.getMockTestLearningProcess());

        //Creating process JSON
        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();

        // CREATE THE USER GROUP
        MvcResult result = mvc.perform(post(BASE_URL_LPROCESS+"/"+sLearningProcessTestId+"/group").content(userGroupJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(userGroupName)))
                .andReturn();


        // UPDATE THE USER GROUP NAME
        UserGroup userGroupToUpdate = this.mapper.readValue(result.getResponse().getContentAsByteArray(), UserGroup.class);
        userGroupToUpdate.setName(userGroupNameUpdated);

        byte[] userGroupUpdatableJSON = this.mapper.writeValueAsString(userGroupToUpdate).getBytes();

        // UPDATE THE USER GROUP
        MvcResult resultUpdated = mvc.perform(put(BASE_URL+"/"+userGroupToUpdate.getId())
                .content(userGroupUpdatableJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userGroupNameUpdated)))
                .andReturn();
    }

    /**
     * Should update a user group from lprocess with id=1
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username="supervisor",roles={"PM","ADMIN"})
    public void shouldAddLearningStudentToUserGroup() throws Exception {

        String sLearningProcessTestId = "1";
        String userGroupName = "Group 1";

        UserGroup userGroupObject = new UserGroup();
        userGroupObject.setName(userGroupName);
        userGroupObject.setLearningStudentList(new ArrayList<>());
        userGroupObject.setLearningProcess(this.getMockTestLearningProcess());

        //Creating process JSON
        byte[] userGroupJSON = this.mapper.writeValueAsString(userGroupObject).getBytes();

        // CREATE THE USER GROUP
        MvcResult result = mvc.perform(post(BASE_URL_LPROCESS+"/"+sLearningProcessTestId+"/group").content(userGroupJSON)
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
        MvcResult resultUpdated = mvc.perform(put(BASE_URL+"/student/add/"+userGroupToUpdate.getId())
                .content(userGroupUpdatableJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
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
    private LearningStudent getMockTestStudent() {


        LearningStudent testStudent = new LearningStudent();

        testStudent.setFirstName("studentName1");
        testStudent.setLastName("studentName1");
        testStudent.setUsername(usernameStudent1);

        return testStudent;
    }
}
