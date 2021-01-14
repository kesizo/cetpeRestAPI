package com.kesizo.cetpe.backend.restapi.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.app.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.app.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.app.model.RubricType;
import com.kesizo.cetpe.backend.restapi.app.service.AssessmentRubricService;
import com.kesizo.cetpe.backend.restapi.app.service.LearningProcessService;
import com.kesizo.cetpe.backend.restapi.app.service.RubricTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class AssessmentRubricController {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentRubricController.class);

    @Autowired
    private LearningProcessService _learningProcessService;

    @Autowired
    private AssessmentRubricService _assessmentRubricService;

    @Autowired
    private RubricTypeService _rubricTypeService;

    @Autowired
    private ObjectMapper mapper; // Used to convert Objects to/from JSON



    @RequestMapping(value = "/api/cetpe/rubric", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public List<AssessmentRubric> assessmentRubricIndex(){

        return _assessmentRubricService.getAllAssessmentRubrics();
    }

    @RequestMapping(value = "/api/cetpe/rubric/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public AssessmentRubric assessmentRubricById(@PathVariable String id){
        long assessmentRubricId = 0;
        try {
            assessmentRubricId = Long.parseLong(id);
        }
        catch (NumberFormatException nfe) {
            logger.warn("Error id parameter is not numeric");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error id parameter is not numeric", nfe);
        }

        AssessmentRubric currentRubric = _assessmentRubricService.getAssessmentRubricById(assessmentRubricId);
        if (currentRubric==null) {
            throw new ResourceNotFoundException("Rubric with id= " + id + " not found");
        }
        return currentRubric;
    }

    @RequestMapping(value = "/api/cetpe/rubrics/by/lprocess/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public List<AssessmentRubric> rubricsByLearningProcessId(@PathVariable String id){

        long learningProcessId = 0;
        try {
            learningProcessId = Long.parseLong(id);
        }
        catch (NumberFormatException nfe) {
            logger.warn("Error id parameter is not numeric");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error id parameter is not numeric", nfe);
        }
        List<AssessmentRubric> rubricList =_assessmentRubricService.getAssessmentRubricsByLearningProcessId(learningProcessId);
        if (rubricList==null) {
            throw new ResourceNotFoundException("Rubrics associated to learning process with id= " + id + " not found");
        }
        return rubricList;
    }

    @PostMapping("/api/cetpe/rubric")
    @ResponseStatus(HttpStatus.CREATED) // Otherwise it returns 200 because is the default code for @RestController
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN')")
    public AssessmentRubric create(@RequestBody Map<String, Object> body) {

        try {
            String title = body.get("title").toString();

            LocalDateTime starting_date_time = LocalDateTime.parse(body.get("starting_date_time").toString());
            LocalDateTime end_date_time = LocalDateTime.parse(body.get("end_date_time").toString());

            int rank = Integer.parseInt(body.get("rank").toString());
            boolean enabled = Boolean.parseBoolean(body.get("enabled").toString());

            Object rubricType = body.get("rubricType");
            Object learningProcess = body.get("learningProcess");

            String rubricTypeJSON= null;
            String learningProcessJSON = null;

            RubricType rubricTypeObject = null;
            LearningProcess learningProcessObject = null;

            try {
                rubricTypeJSON = mapper.writeValueAsString(rubricType);
                rubricTypeObject = mapper.readValue(rubricTypeJSON, RubricType.class);

                learningProcessJSON = mapper.writeValueAsString(learningProcess);
                learningProcessObject = mapper.readValue(learningProcessJSON, LearningProcess.class);

            } catch (JsonProcessingException e) {
                logger.warn("Error when converting passed mandatory parameter to JSON provided when creating rubric");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting passed mandatory parameter to JSON provided when creating rubric", e);
            } catch (IOException e) {
                logger.warn("Error when converting JSON parameter to model object when creating rubric");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting JSON parameter to model object when creating rubric", e);
            }

            return _assessmentRubricService.createAssessmentRubric(title, starting_date_time,
                                                                end_date_time, rank, enabled,
                                                                _rubricTypeService.getRubricTypeById(rubricTypeObject != null ? rubricTypeObject.getId() : null ),
                                                                _learningProcessService.getLearningProcessById(learningProcessObject != null ? learningProcessObject.getId() : null ));
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when creating Assessment Rubric ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when creating Assessment Rubric", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @PutMapping("/api/cetpe/rubric/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN')")
    public AssessmentRubric update(@PathVariable String id, @RequestBody Map<String, Object> body) {

        try {
            long assessmentRubricId = Long.parseLong(id);
            String title = body.get("title").toString();

            LocalDateTime starting_date_time = LocalDateTime.parse(body.get("starting_date_time").toString());
            LocalDateTime end_date_time = LocalDateTime.parse(body.get("end_date_time").toString());

            int rank = Integer.parseInt(body.get("rank").toString());
            boolean enabled = Boolean.parseBoolean(body.get("enabled").toString());

            Object rubricType = body.get("rubricType");
            Object learningProcess = body.get("learningProcess");

            String rubricTypeJSON = null;
            String learningProcessJSON = null;

            RubricType rubricTypeObject = null;
            LearningProcess learningProcessObject = null;

            try {
                rubricTypeJSON = mapper.writeValueAsString(rubricType);
                rubricTypeObject = mapper.readValue(rubricTypeJSON, RubricType.class);

                learningProcessJSON = mapper.writeValueAsString(learningProcess);
                learningProcessObject = mapper.readValue(learningProcessJSON, LearningProcess.class);

            } catch (JsonProcessingException e) {
                logger.warn("Error when converting passed mandatory parameter to JSON provided when updating rubric");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting passed mandatory parameter to JSON provided when updating rubric", e);
            } catch (IOException e) {
                logger.warn("Error when converting JSON parameter to model object when updating rubric");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting JSON parameter to model object when updating rubric", e);
            }

            return _assessmentRubricService.updateAssessmentRubric(assessmentRubricId, title, starting_date_time,
                    end_date_time, rank, enabled,
                    _rubricTypeService.getRubricTypeById(rubricTypeObject != null ? rubricTypeObject.getId() : null),
                    _learningProcessService.getLearningProcessById(learningProcessObject != null ? learningProcessObject.getId() : null));

        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when updating Assessment Rubric ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when updating Assessment Rubric", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }

    }

    @DeleteMapping("/api/cetpe/rubric/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN')")
    public boolean delete(@PathVariable String id) {
        long assessmentRubricId = Long.parseLong(id);

        return _assessmentRubricService.deleteAssessmentRubric(assessmentRubricId);

    }


}
