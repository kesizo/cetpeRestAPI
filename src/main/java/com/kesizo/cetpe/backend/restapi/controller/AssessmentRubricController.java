package com.kesizo.cetpe.backend.restapi.controller;

import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.service.AssessmentRubricService;
import com.kesizo.cetpe.backend.restapi.service.LearningProcessService;
import com.kesizo.cetpe.backend.restapi.service.RubricTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.kesizo.cetpe.backend.restapi.util.Constants.DATE_FORMATTER;

@RestController
public class AssessmentRubricController {

    @Autowired
    private LearningProcessService _learningProcessService;

    @Autowired
    private AssessmentRubricService _assessmentRubricService;

    @Autowired
    private RubricTypeService _rubricTypeService;

    @RequestMapping(value = "/api/cetpe/lprocess/rubric", method = RequestMethod.GET)
    public List<AssessmentRubric> assessmentRubricIndex(){

        return _assessmentRubricService.getAllAssessmentRubrics();
    }

    @RequestMapping(value = "/api/cetpe/lprocess/rubric/{id}", method = RequestMethod.GET)
    public AssessmentRubric assessmentRubricById(@PathVariable String id){
        long assessmentRubricId = Long.parseLong(id);
        return _assessmentRubricService.getAssessmentRubricById(assessmentRubricId);
    }

    @RequestMapping(value = "/api/cetpe/lprocess/rubrics/by/lprocess/{id}", method = RequestMethod.GET)
    public List<AssessmentRubric> rubricsByLearningProcessId(@PathVariable String id){
        long learningProcessId = Long.parseLong(id);
        return _assessmentRubricService.getAssessmentRubricsByLearningProcessId(learningProcessId);
    }


    @PostMapping("/api/cetpe/lprocess/rubric")
    public AssessmentRubric create(@RequestBody Map<String, String> body) {

        String title = body.get("title");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        LocalDateTime starting_date_time = LocalDateTime.parse(body.get("starting_date_time"), formatter);
        LocalDateTime end_date_time = LocalDateTime.parse(body.get("end_date_time"), formatter);
        int rank = Integer.parseInt(body.get("rank"));

        long rubricType_id = Long.parseLong(body.get("rubricType_id"));
        long learningProcess_id = Long.parseLong(body.get("learningProcess_id"));

        return _assessmentRubricService.createAssessmentRubric(title, starting_date_time,
                                                                end_date_time, rank,
                                                                _rubricTypeService.getRubricTypeById(rubricType_id),
                                                                _learningProcessService.getLearningProcessById(learningProcess_id));
    }

    @PutMapping("/api/cetpe/lprocess/rubric/{id}")
    public AssessmentRubric update(@PathVariable String id, @RequestBody Map<String, String> body) {

        long assessmentRubricId = Long.parseLong(id);
        String title = body.get("title");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        LocalDateTime starting_date_time = LocalDateTime.parse(body.get("starting_date_time"), formatter);
        LocalDateTime end_date_time = LocalDateTime.parse(body.get("end_date_time"), formatter);
        int rank = Integer.parseInt(body.get("rank"));

        long rubricType_id = Long.parseLong(body.get("rubricType_id"));
        long learningProcess_id = Long.parseLong(body.get("learningProcess_id"));

        return _assessmentRubricService.updateAssessmentRubric(assessmentRubricId, title, starting_date_time,
                end_date_time, rank,
                _rubricTypeService.getRubricTypeById(rubricType_id),
                _learningProcessService.getLearningProcessById(learningProcess_id));


    }

    @DeleteMapping("/api/cetpe/lprocess/rubric/{id}")
    public boolean delete(@PathVariable String id) {
        long assessmentRubricId = Long.parseLong(id);

        return _assessmentRubricService.deleteAssessmentRubric(assessmentRubricId);

    }


}
