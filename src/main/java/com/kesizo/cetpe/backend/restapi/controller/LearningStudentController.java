package com.kesizo.cetpe.backend.restapi.controller;

import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.service.LearningStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class LearningStudentController {

    @Autowired
    private LearningStudentService _learningStudentService;

    private Logger logger = LoggerFactory.getLogger(LearningStudentController.class);

    @RequestMapping(value = "/api/cetpe/lstudent", method = RequestMethod.GET)
    public List<LearningStudent> learningStudentsIndex(){
        return _learningStudentService.getAllLearningStudents();
    }

    @RequestMapping(value = "/api/cetpe/lstudent/{username}", method = RequestMethod.GET)
    public LearningStudent learningStudentByUsername(@PathVariable String username){

        final LearningStudent currentStudent;

        currentStudent = _learningStudentService.getLearningStudentByUserName(username);

        if (currentStudent==null) {
            throw new ResourceNotFoundException("Student with username= " + username + " not found");
        }
        return currentStudent;
    }


    @PostMapping("/api/cetpe/lstudent")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningStudent create(@RequestBody Map<String, Object> body) {

        try {
            String username = body.get("username").toString();
            String firstName = body.get("firstName").toString();
            String lastName = body.get("lastName").toString();

            return _learningStudentService.createLearningStudent(username,firstName, lastName);
        }
        catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when creating student");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when creating student", np);
        }
        catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }


    }

    @PutMapping("/api/cetpe/lstudent/{username}")
    public LearningStudent update(@PathVariable String username, @RequestBody Map<String, Object> body) {

        try {
            String firstName = body.get("firstName").toString();
            String lastName = body.get("lastName").toString();

            return _learningStudentService.updateLearningStudent(username,firstName, lastName);
        }
        catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when creating student");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when creating student", np);
        }
        catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }

    }


    @DeleteMapping("/api/cetpe/lstudent/{username}")
    public boolean delete(@PathVariable String username) {

        if (username==null || username.isEmpty() || username.length()<3 || username.length()>256) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error student username is not valid");
        }


        return _learningStudentService.deleteLearningStudent(username);

    }

}
