package com.kesizo.cetpe.backend.restapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.model.UserGroup;
import com.kesizo.cetpe.backend.restapi.service.LearningProcessService;
import com.kesizo.cetpe.backend.restapi.service.LearningStudentService;
import com.kesizo.cetpe.backend.restapi.service.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class UserGroupController {

    private Logger logger = LoggerFactory.getLogger(UserGroupController.class);

    @Autowired
    private UserGroupService _userGroupService;

    @Autowired
    private LearningProcessService _learningProcessService;

    @Autowired
    private LearningStudentService _learningStudentService;

    @Autowired
    private ObjectMapper mapper; // Used to convert Objects to/from JSON

    @RequestMapping(value = "/api/cetpe/group", method = RequestMethod.GET)
    public List<UserGroup> userGroupsIndex(){

        return _userGroupService.getAllUserGroups();
    }


    @RequestMapping(value = "/api/cetpe/group/{id}", method = RequestMethod.GET)
    public UserGroup userGroupById(@PathVariable String id){
        try{
            long userGroupId = Long.parseLong(id);
            return _userGroupService.getUserGroupById(userGroupId);

        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when getting UserGroup ");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when getting UserGroup", np);
        } catch (NumberFormatException nfe) {
            logger.warn("Error Item Rubric is a mandatory parameter and it has been provided as null when getting UserGroup ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Item Rubric mandatory parameter null provided when getting UserGroup", nfe);
        }
    }

    //http://localhost:8083/api/cetpe/lprocess/group?id_lprocess=1
    @GetMapping("/api/cetpe/lprocess/group")
    @ResponseBody
    public List<UserGroup> userGroupsByLearningProcessId(@RequestParam(required = false) String id_lprocess)
    {
        List<UserGroup> userGroupList = new ArrayList<>();
        if (null==id_lprocess) {

            logger.warn("problem retrieving user groups by lprocess_id because it is null");
        }
        else {

            userGroupList = _userGroupService.getUserGroupsByLearningProcessId(Long.parseLong(id_lprocess));
        }
        return userGroupList;
    }


    @PostMapping("/api/cetpe/lprocess/{id}/group")
    @ResponseStatus(HttpStatus.CREATED) // Otherwise it returns 200 because is the default code for @RestController§
    public UserGroup create(@PathVariable String id,@RequestBody Map<String, Object> body) {

        try {
            String name = body.get("name").toString();
            long learningProcess_id = Long.parseLong(id);

            return _userGroupService.createUserGroup(name,_learningProcessService.getLearningProcessById(learningProcess_id));

        } catch (NumberFormatException nfe) {
            logger.warn("Error learningProcessId is a mandatory parameter and it has been provided as not number when creating UserGroup ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error name is a mandatory parameter and null is provided when creating UserGroup", nfe);
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when creating UserGroup");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when creating UserGroup", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @PutMapping("/api/cetpe/group/{id}")
    public UserGroup update(@PathVariable String id, @RequestBody Map<String, Object> body) {

        try {

            long userGroupId = Long.parseLong(id);
            String name = body.get("name").toString();

            return _userGroupService.updateUserGroup(userGroupId,name);

        } catch (NumberFormatException nfe) {
            logger.warn("Error id is a mandatory parameter and it has been provided as not number when updating UserGroup ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error name is a mandatory parameter and null is provided when updating UserGroup", nfe);
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when updating UserGroup");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when updating UserGroup", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @PutMapping("/api/cetpe/group/student/add/{id}")
    public UserGroup updateAddLearningStudent(@PathVariable String id, @RequestBody Map<String, Object> body) {

        try {
            long userGroupId = Long.parseLong(id);

            Object studentList = body.get("learningStudentList"); //as indicated in getters and setters of the model

            String studentToAddJSON= null;
            List<LearningStudent> studentToAddObject = null;

            try {
                studentToAddJSON = mapper.writeValueAsString(studentList);
                studentToAddObject = Arrays.asList(mapper.readValue(studentToAddJSON, LearningStudent[].class)); // This is a faster option

            } catch (JsonProcessingException e) {
                logger.warn("Error when converting passed mandatory parameter to JSON provided when updating UserGroup");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting passed mandatory parameter to JSON provided when updating UserGroup by adding student", e);
            } catch (IOException e) {
                logger.warn("Error when converting JSON parameter to model object when updating UserGroup by adding student");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting JSON parameter to model object when updating UserGroup by adding student", e);
            }

            return _userGroupService.updateByAddingLearningStudent(userGroupId,studentToAddObject.get(studentToAddObject.size()-1));

        } catch (NumberFormatException nfe) {
            logger.warn("Error id is a mandatory parameter and it has been provided as not number when updating UserGroup by adding student ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error name is a mandatory parameter and null is provided when updating UserGroup by adding student", nfe);
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when updating UserGroup by adding student");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when updating UserGroup by adding student", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @PutMapping("/api/cetpe/group/student/remove/{id}")
    public boolean updateRemoveLearningStudent(@PathVariable String id, @RequestBody Map<String, Object> body) {
        try {
            long userGroupId = Long.parseLong(id);
            boolean isAllRemoved = true;

            Object studentList = body.get("learningStudentList"); //as indicated in getters and setters of the model
            String studentToRemoveJSON= null;
            List<LearningStudent> studentToRemoveObject = null;

            try {
                studentToRemoveJSON = mapper.writeValueAsString(studentList);
                studentToRemoveObject = Arrays.asList(mapper.readValue(studentToRemoveJSON, LearningStudent[].class));

            } catch (JsonProcessingException e) {
                logger.warn("Error when converting passed mandatory parameter to JSON provided when updating UserGroup by removing student");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting passed mandatory parameter to JSON provided when updating UserGroup by removing student", e);
            } catch (IOException e) {
                logger.warn("Error when converting JSON parameter to model object when updating UserGroup by removing student");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting JSON parameter to model object when updating UserGroup by removing student", e);
            }

            for (int i=0; i<studentToRemoveObject.size() ; i++) {

                isAllRemoved = isAllRemoved && (null!=_userGroupService.updateByRemovingLearningStudent(userGroupId,studentToRemoveObject.get(i)));
            }
            return isAllRemoved;
        } catch (NumberFormatException nfe) {
            logger.warn("Error id is a mandatory parameter and it has been provided as not number when updating UserGroup by removing student ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error name is a mandatory parameter and null is provided when updating UserGroup by removing student", nfe);
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when updating UserGroup by removing student");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when updating UserGroup by removing student", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @PutMapping("/api/cetpe/group/authorized/{id}")
    public UserGroup assignAuthorizedCandidateToUsergroup(@PathVariable String id, @RequestBody Map<String, Object> body) {
        try {
            long userGroupId = Long.parseLong(id);
            String name = body.get("name").toString();
            String authorizedStudent = body.get("authorizedStudent").toString();

            LearningStudent authorizedCandidate = _learningStudentService.getLearningStudentByUserName(authorizedStudent);
            if (authorizedCandidate==null) {
                logger.warn("Authorized candidate username doesn't exist");
                return null;
            }
            else {
                return _userGroupService.updateUserGroupAuthorized(userGroupId, authorizedCandidate);
            }

        } catch (NumberFormatException nfe) {
            logger.warn("Error id  a mandatory parameter and it has been provided as not number when updating UserGroup by assigning authorized student ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error name is a mandatory parameter and null is provided when updating UserGroup by assigning authorized student", nfe);
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when updating UserGroup by assigning authorized");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when updating UserGroup by assigning authorized", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }

    }

    @DeleteMapping("/api/cetpe/group/{id}")
    public boolean delete(@PathVariable String id) {

        try {
            long userGroupId = Long.parseLong(id);

            return _userGroupService.deleteUserGroup(userGroupId);
        } catch (NumberFormatException nfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error id parameter is not numeric", nfe);
        }

    }


}
