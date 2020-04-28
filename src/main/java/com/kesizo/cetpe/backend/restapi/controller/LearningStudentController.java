package com.kesizo.cetpe.backend.restapi.controller;

import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.service.LearningStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/api/cetpe/lstudent/username/{username}", method = RequestMethod.GET)
    public LearningStudent learningStudentByUsername(@PathVariable String username){

        return _learningStudentService.getLearningStudentByUserName(username);
    }


    @PostMapping("/api/cetpe/lstudent")
    public LearningStudent create(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");

        return _learningStudentService.createLearningStudent(username,firstName, lastName);
    }

    @PutMapping("/api/cetpe/lstudent/{id}")
    public LearningStudent update(@PathVariable String username, @RequestBody Map<String, String> body) {

        String firstName = body.get("firstName");
        String lastName = body.get("lastName");

        return _learningStudentService.updateLearningStudent(username,firstName, lastName);

    }


    @DeleteMapping("/api/cetpe/lstudent/{id}")
    public boolean delete(@PathVariable String username) {

        return _learningStudentService.deleteLearningStudent(username);

    }

}
