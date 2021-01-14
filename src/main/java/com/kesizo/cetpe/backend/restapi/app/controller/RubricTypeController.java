package com.kesizo.cetpe.backend.restapi.app.controller;


import com.kesizo.cetpe.backend.restapi.app.model.RubricType;
import com.kesizo.cetpe.backend.restapi.app.service.RubricTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class RubricTypeController {

    @Autowired
    private RubricTypeService rubricTypeService;

    @RequestMapping(value = "/api/cetpe/rubric/types", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM')or hasRole('ADMIN')")
    public List<RubricType> cetpeRubricTypesIndex(){
        return rubricTypeService.getAllRubricTypes();
    }


    @GetMapping("/api/cetpe/rubric/types/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('PM')or hasRole('ADMIN')")
    public RubricType rubricTypeById(@PathVariable String id){

        long rubricTypeId;
        try {
            rubricTypeId = Long.parseLong(id);
        } catch (NumberFormatException nfe) {
           throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error id parameter is not numeric", nfe);
        }

        RubricType retrieved = rubricTypeService.getRubricTypeById(rubricTypeId);

        if (retrieved==null) {
            throw new ResourceNotFoundException("Rubric type with id= " + id + " not found");
        }

        return retrieved;
    }

}
