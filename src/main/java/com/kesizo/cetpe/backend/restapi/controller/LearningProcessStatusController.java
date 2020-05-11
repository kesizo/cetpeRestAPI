package com.kesizo.cetpe.backend.restapi.controller;


import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.service.LearningProcessStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LearningProcessStatusController {

    @Autowired
    private LearningProcessStatusService learningProcessStatusService;

    @RequestMapping(value = "/api/cetpe/status", method = RequestMethod.GET)
    public List<LearningProcessStatus> cetpeLearningProcessStatusIndex(){
        return learningProcessStatusService.getAllLearningProcessStatus();
    }

    //Operations with the blogs. Retrieve (GET), Update (PUT), Remove (DELETE)
    @GetMapping("/api/cetpe/status/{id}")
    public LearningProcessStatus statusTypeById(@PathVariable String id){

        long learningProcessStatusId ;
        try {
            learningProcessStatusId = Long.parseLong(id);
        } catch (NumberFormatException nfe) {
            throw new ResourceNotFoundException("The id provided is not a valid number id");
        }


        LearningProcessStatus retrieved = learningProcessStatusService.getLearningProcessStatusById(learningProcessStatusId);

        if (retrieved==null) {
            throw new ResourceNotFoundException("Status type with id= " + id + " not found");
        }

        return retrieved;
    }

}
