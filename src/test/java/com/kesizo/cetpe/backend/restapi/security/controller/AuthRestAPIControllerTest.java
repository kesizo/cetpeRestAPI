package com.kesizo.cetpe.backend.restapi.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.security.message.request.LoginForm;
import com.kesizo.cetpe.backend.restapi.security.message.request.SignUpForm;
import com.kesizo.cetpe.backend.restapi.security.message.response.JwtResponse;
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

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
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
public class AuthRestAPIControllerTest {


    private static final String BASE_URL = "/api/auth";
    private static final String TEST_USER_URL = "/api/test/user";

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
                "DELETE FROM learning_student;"+
                "DELETE FROM user_roles;" +
                "DELETE FROM roles;"+
                "DELETE FROM users;");


        jdbcTemplate.execute(
                "INSERT INTO learning_process_status(id,name,description) values (0,'CEPTE Process Created','Initial status for a peer evaluation process');" +
                        "INSERT INTO learning_process_status(id,name,description) values (1,'CEPTE Process Available','The Learning process can be accessed by students');" +
                        "INSERT INTO learning_process_status(id,name,description) values (2,'CEPTE Process Finished','The Learning process is finished');" +
                        "INSERT INTO learning_process_status(id,name,description) values (3,'CEPTE Process results available','The Learning process is finished and results are published');");


        jdbcTemplate.execute(
                "INSERT INTO roles(name) VALUES('ROLE_USER');" +
                        "INSERT INTO roles(name) VALUES('ROLE_PM');" +
                        "INSERT INTO roles(name) VALUES('ROLE_ADMIN');");
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
                "DELETE FROM learning_student;" +
                "DELETE FROM user_roles;" +
                "DELETE FROM roles;"+
                "DELETE FROM users;");
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(mvc).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    @Test
    public void shouldRegisterAdminUser() throws Exception {

        // Creating process JSON
        byte[] adminUserJSON = this.mapper.writeValueAsString(mockSignUpFormAdminUser()).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$", is("User registered successfully!")))
                .andReturn();
    }

    @Test
    public void shouldBadRequestWhenTryingToRegister2UsersWithTheSameUserName() throws Exception {

        SignUpForm currentUser =  mockSignUpFormAdminUser();
        // Creating process JSON
        byte[] adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$", is("User registered successfully!")))
                .andReturn();

        currentUser.setName("adminName2");
        currentUser.setEmail("admin2@email.com");
        adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$", is("Fail -> Username is already taken!")))
                .andReturn();
    }

    @Test
    public void shouldBadRequestWhenTryingToRegister2UsersWithTheSameEmail() throws Exception {

        SignUpForm currentUser =  mockSignUpFormAdminUser();
        // Creating process JSON
        byte[] adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$", is("User registered successfully!")))
                .andReturn();

        currentUser.setUsername("adminName2");
        adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$", is("Fail -> Email is already in use!")))
                .andReturn();
    }

    @Test
    public void shouldBadRequestWhenTryingToRegisterWithEmailNullOrEmpty() throws Exception {

        SignUpForm currentUser =  mockSignUpFormAdminUser();
        currentUser.setEmail(null);

        // Creating process JSON
        byte[] adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        currentUser.setEmail("");
        adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void shouldBadRequestWhenTryingToRegisterWithUserNameNullOrEmpty() throws Exception {

        SignUpForm currentUser =  mockSignUpFormAdminUser();
        currentUser.setUsername(null);

        // Creating process JSON
        byte[] adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        currentUser.setUsername("");
        adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void shouldRegisterAsRoleUserWhenTryingToRegisterWithRoleEmpty() throws Exception {

        SignUpForm currentUser =  mockSignUpFormRegularUser();
        currentUser.setRole(new HashSet<>());

        // Creating process JSON
        byte[] adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        MvcResult result =  mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$", is("User registered successfully!")))
                .andReturn();
    }

    @Test
    public void shouldBadRequestWhenTryingToRegisterWithRoleNull() throws Exception {

        SignUpForm currentUser =  mockSignUpFormRegularUser();
        currentUser.setRole(null);

        // Creating process JSON
        byte[] adminUserJSON = this.mapper.writeValueAsString(currentUser).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(adminUserJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$", is("Fail -> No role is related to the sign up request!")))
                .andReturn();
    }

    @Test
    public void shouldReturnNothingGetStatusTypeByInvalidId() throws Exception {
        mvc.perform(get(BASE_URL + "/5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldSignInARegisterUser() throws Exception {

        // Creating process JSON
        byte[] userJSON = this.mapper.writeValueAsString(mockSignUpFormRegularUser()).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(userJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$", is("User registered successfully!")))
                .andReturn();

        byte[] userLoginJSON = this.mapper.writeValueAsString(mockLogInFormRegularUser()).getBytes();

        MvcResult resultJWT = mvc.perform(post(BASE_URL+"/signin").content(userLoginJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType",is("Bearer")))
                .andReturn();
    }

    @Test
    public void shouldAccessProtectedTestURLAsSignedInUser() throws Exception {

        // Creating process JSON
        byte[] userJSON = this.mapper.writeValueAsString(mockSignUpFormRegularUser()).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(userJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$", is("User registered successfully!")))
                .andReturn();

        byte[] userLoginJSON = this.mapper.writeValueAsString(mockLogInFormRegularUser()).getBytes();

        MvcResult resultJWT = mvc.perform(post(BASE_URL+"/signin").content(userLoginJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType",is("Bearer")))
                .andReturn();

        JwtResponse jwtResponse = this.mapper.readValue(resultJWT.getResponse().getContentAsByteArray(), JwtResponse.class);

        mvc.perform(get(TEST_USER_URL)
                    .header("Authorization", jwtResponse.getTokenType()+" "+jwtResponse.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
    }

    @Test
    public void shouldUnauthorizedAccessProtectedTestURLAsSignedInUserWithoutRequestingJWT() throws Exception {

        // Creating process JSON
        byte[] userJSON = this.mapper.writeValueAsString(mockSignUpFormRegularUser()).getBytes();

        mvc.perform(post(BASE_URL+"/signup").content(userJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$", is("User registered successfully!")))
                .andReturn();

        byte[] userLoginJSON = this.mapper.writeValueAsString(mockLogInFormRegularUser()).getBytes();

        mvc.perform(get(TEST_USER_URL)
               // .header("Authorization", jwtResponse.getTokenType()+" "+jwtResponse.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void shouldUnauthorizedRequestWhenTryingToLogInWithNoRegisteredUser() throws Exception {

        byte[] userLoginJSON = this.mapper.writeValueAsString(mockLogInFormRegularUser()).getBytes();

        MvcResult resultJWT = mvc.perform(post(BASE_URL+"/signin").content(userLoginJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldBadRequestWhenTryingToLogInWithUsernameNullOrEmpty() throws Exception {

        LoginForm userToLog = mockLogInFormRegularUser();
        userToLog.setUsername(null);
        byte[] userLoginJSON = this.mapper.writeValueAsString(userToLog).getBytes();

        MvcResult resultJWT = mvc.perform(post(BASE_URL+"/signin").content(userLoginJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andReturn();

        userToLog.setUsername("");
        userLoginJSON = this.mapper.writeValueAsString(userToLog).getBytes();

        resultJWT = mvc.perform(post(BASE_URL+"/signin").content(userLoginJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andReturn();
    }

    @Test
    public void shouldBadRequestWhenTryingToLogInWithPasswordNullOrEmpty() throws Exception {

        LoginForm userToLog = mockLogInFormRegularUser();
        userToLog.setPassword(null);
        byte[] userLoginJSON = this.mapper.writeValueAsString(userToLog).getBytes();

        MvcResult resultJWT = mvc.perform(post(BASE_URL+"/signin").content(userLoginJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andReturn();

        userToLog.setPassword("");
        userLoginJSON = this.mapper.writeValueAsString(userToLog).getBytes();

        resultJWT = mvc.perform(post(BASE_URL+"/signin").content(userLoginJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andReturn();
    }

    private static SignUpForm mockSignUpFormAdminUser() {

        SignUpForm sufUser = new SignUpForm();
        sufUser.setName("adminName");
        sufUser.setUsername("admin");
        sufUser.setEmail("admin@email.com");
        sufUser.setPassword("adminpassword");
        HashSet<String> roles = new HashSet<>();
        roles.add("admin");
        sufUser.setRole(roles);

        return sufUser;
    }

    private static SignUpForm mockSignUpFormRegularUser() {

        SignUpForm sufUser = new SignUpForm();
        sufUser.setName("regularName");
        sufUser.setUsername("regularUsername");
        sufUser.setEmail("user@email.com");
        sufUser.setPassword("userpassword");
        HashSet<String> roles = new HashSet<>();
        roles.add("user");
        sufUser.setRole(roles);

        return sufUser;
    }

    private static LoginForm mockLogInFormRegularUser() {

        LoginForm loginUser = new LoginForm();
        loginUser.setUsername("regularUsername");
        loginUser.setPassword("userpassword");
        return loginUser;
    }


}

