package com.kesizo.cetpe.backend.restapi.controller;

import com.kesizo.cetpe.backend.restapi.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.service.LearningSupervisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class LearningSupervisorController {

    @Autowired
    private LearningSupervisorService _learningSupervisorService;

    private Logger logger = LoggerFactory.getLogger(LearningSupervisorController.class);

    @RequestMapping(value = "/api/cetpe/lsupervisor", method = RequestMethod.GET)
    public List<LearningSupervisor> learningSupervisorsIndex(){
        return _learningSupervisorService.getAllLearningSupervisors();
    }

    @RequestMapping(value = "/api/cetpe/lsupervisor/username/{username}", method = RequestMethod.GET)
    public LearningSupervisor learningSupervisorsByUsername(@PathVariable String username){

        return _learningSupervisorService.getLearningSupervisorByUserName(username);
    }


    @PostMapping("/api/cetpe/lsupervisor")
    public LearningSupervisor create(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");

        return _learningSupervisorService.createLearningSupervisor(username,firstName, lastName);
    }

    @PutMapping("/api/cetpe/lsupervisor/{id}")
    public LearningSupervisor update(@PathVariable String username, @RequestBody Map<String, String> body) {

        String firstName = body.get("firstName");
        String lastName = body.get("lastName");

        return _learningSupervisorService.updateLearningSupervisor(username,firstName, lastName);

    }

    @DeleteMapping("/api/cetpe/lsupervisor/{id}")
    public boolean delete(@PathVariable String username) {

        return _learningSupervisorService.deleteLearningSupervisor(username);

    }

}
