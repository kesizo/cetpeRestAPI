package com.kesizo.cetpe.backend.restapi.controller;

import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.service.AssessmentRubricService;
import com.kesizo.cetpe.backend.restapi.service.LearningProcessService;
import com.kesizo.cetpe.backend.restapi.service.LearningProcessStatusService;
import com.kesizo.cetpe.backend.restapi.service.RubricTypeService;
import com.kesizo.cetpe.backend.restapi.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.kesizo.cetpe.backend.restapi.util.Constants.DATE_FORMATTER;

@RestController
public class LearningProcessController {

    @Autowired
    private LearningProcessService _learningProcessService;

    @Autowired
    private LearningProcessStatusService _learningProcessStatusService;

    @Autowired
    private AssessmentRubricService _assessmentRubricService;

    @Autowired
    private RubricTypeService _rubricTypeService;


    @RequestMapping(value = "/api/cetpe/lprocess", method = RequestMethod.GET)
    public List<LearningProcess> cetpeLearningProcessIndex(){

        return _learningProcessService.getAllLearningProcess();
    }

    @RequestMapping(value = "/api/cetpe/lprocess/{id}", method = RequestMethod.GET)
    public LearningProcess cetpeLearningProcessById(@PathVariable String id){
        long learningProcessId = Long.parseLong(id);
        return _learningProcessService.getLearningProcessById(learningProcessId);
    }


    /**
     * This transactional method invokes LearningProcess and AssessmentRubric services to create a new learning
     * process. Note: When annotating both the RestController method and the Service layer method with @Transactional
     * annotation if an error takes place after the save() method was called and a new record was created in a database,
     * the transaction will be rolled back and the change will be undone.
     *
     * @param body
     * @return
     */
    @PostMapping("/api/cetpe/lprocess")
    public LearningProcess create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String description = body.get("description");

        LearningProcess currentLearningProcess = _learningProcessService.createLearningProcess(
                name, description,
                LocalDateTime.now(), LocalDateTime.now(),
                false, false,
                false, false,
                0.0f , 0.0f,
                0.0f, 0.0f,
                20,20,
                20,20,
                20, _learningProcessStatusService.getLearningProcessStatusById(0));


        IntStream.rangeClosed(1,4).forEach(rubricTypeId -> _assessmentRubricService.createAssessmentRubric(
                Constants.RUBRIC_TITLE_DEFAULT + rubricTypeId,
                    currentLearningProcess.getStarting_date_time(),
                    currentLearningProcess.getEnd_date_time(),
                    Constants.RUBRIC_DEFAULT_RANK,
                    _rubricTypeService.getRubricTypeById(rubricTypeId),
                    currentLearningProcess)
        );

        return currentLearningProcess;
    }

    @PutMapping("/api/cetpe/lprocess/{id}")
    public LearningProcess update(@PathVariable String id, @RequestBody Map<String, String> body) {

        long learningProcessId = Long.parseLong(id);
        String name = body.get("name");
        String description = body.get("description");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        LocalDateTime starting_date_time = LocalDateTime.parse(body.get("starting_date_time"), formatter);
        LocalDateTime end_date_time = LocalDateTime.parse(body.get("end_date_time"), formatter);

        boolean is_cal1_available = Boolean.parseBoolean(body.get("is_cal1_available"));
        boolean is_cal2_available = Boolean.parseBoolean(body.get("is_cal2_available"));
        boolean is_cal3_available = Boolean.parseBoolean(body.get("is_cal3_available"));
        boolean is_calF_available = Boolean.parseBoolean(body.get("is_calF_available"));

        float limit_cal1 = Float.parseFloat(body.get("limit_cal1"));
        float limit_cal2 = Float.parseFloat(body.get("limit_cal2"));
        float limit_rev1 = Float.parseFloat(body.get("limit_rev1"));
        float limit_rev2 = Float.parseFloat(body.get("limit_rev2"));

        int weight_param_A = Integer.parseInt(body.get("weight_param_A"));
        int weight_param_B = Integer.parseInt(body.get("weight_param_B"));
        int weight_param_C = Integer.parseInt(body.get("weight_param_C"));
        int weight_param_D = Integer.parseInt(body.get("weight_param_D"));
        int weight_param_E = Integer.parseInt(body.get("weight_param_E"));

        long status_id = Long.parseLong(body.get("status_id"));

        return _learningProcessService.updateLearningProcess(learningProcessId,
                name, description,
                starting_date_time, end_date_time,
                is_cal1_available, is_cal2_available,
                is_cal3_available, is_calF_available,
                limit_cal1 , limit_cal2,
                limit_rev1, limit_rev2,
                weight_param_A,weight_param_B,
                weight_param_C,weight_param_D,
                weight_param_E,
                _learningProcessStatusService.getLearningProcessStatusById(status_id));
    }

    @DeleteMapping("/api/cetpe/lprocess/{id}")
    public boolean delete(@PathVariable String id) {
        long learningProcessId = Long.parseLong(id);

        return _learningProcessService.deleteLearningProcess(learningProcessId);

    }


}
