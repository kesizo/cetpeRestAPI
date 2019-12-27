package com.kesizo.cetpe.backend.restapi.controller;


import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.service.LearningProcessStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LearningProcessStatusController {

    @Autowired
    private LearningProcessStatusService learningProcessStatusService;

    @RequestMapping(value = "/api/cetpe/lprocess/status", method = RequestMethod.GET)
    public List<LearningProcessStatus> cetpeLearningProcessStatusIndex(){
        return learningProcessStatusService.getAllLearningProcessStatus();
    }

    //Operations with the blogs. Retrieve (GET), Update (PUT), Remove (DELETE)
    @GetMapping("/api/cetpe/lprocess/status/{id}")
    public LearningProcessStatus show(@PathVariable String id){
        long learningProcessStatusId = Long.parseLong(id);
        return learningProcessStatusService.getLearningProcessStatusById(learningProcessStatusId);
    }

}
