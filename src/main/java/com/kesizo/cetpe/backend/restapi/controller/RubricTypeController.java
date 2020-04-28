package com.kesizo.cetpe.backend.restapi.controller;


import com.kesizo.cetpe.backend.restapi.model.RubricType;
import com.kesizo.cetpe.backend.restapi.service.RubricTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RubricTypeController {

    @Autowired
    private RubricTypeService rubricTypeService;

    @RequestMapping(value = "/api/cetpe/rubric/types", method = RequestMethod.GET)
    public List<RubricType> cetpeRubricTypesIndex(){
        return rubricTypeService.getAllRubricTypes();
    }

    //Operations with the blogs. Retrieve (GET), Update (PUT), Remove (DELETE)
    @GetMapping("/api/cetpe/rubric/types/{id}")
    public RubricType rubricTypeById(@PathVariable String id){
        long rubricTypeId = Long.parseLong(id);
        return rubricTypeService.getRubricTypeById(rubricTypeId);
    }

}
