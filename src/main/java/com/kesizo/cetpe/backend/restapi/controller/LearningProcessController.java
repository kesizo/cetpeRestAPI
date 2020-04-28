package com.kesizo.cetpe.backend.restapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.model.UserGroup;
import com.kesizo.cetpe.backend.restapi.service.*;
import com.kesizo.cetpe.backend.restapi.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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

    @Autowired
    private LearningSupervisorService _supervisorService;

    @Autowired
    private ObjectMapper mapper; // Used to convert Objects to/from JSON



    @RequestMapping(value = "/api/cetpe/lprocess", method = RequestMethod.GET)
    public List<LearningProcess> cetpeLearningProcessIndex(){

        return _learningProcessService.getAllLearningProcess();
    }

    @RequestMapping(value = "/api/cetpe/lprocess/{id}", method = RequestMethod.GET)
    public LearningProcess cetpeLearningProcessById(@PathVariable String id){
        long learningProcessId = Long.parseLong(id);
        LearningProcess currentProcess = _learningProcessService.getLearningProcessById(learningProcessId);
        return currentProcess;
    }

    @GetMapping("/api/cetpe/lprocess/supervisor/{username}")
    public List<LearningProcess> getLearningProcessBySupervisorUsername(@PathVariable String username){

        return _learningProcessService.getLearningProcessBySupervisorUsername(username);
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
    public LearningProcess create(@RequestBody Map<String, Object> body) {
        String name = body.get("name").toString();
        String description = body.get("description").toString();
        Object supervisor = body.get("learning_supervisor");

        String supervisorJSON= null;
        LearningSupervisor supervisorObject = null;
        try {
            supervisorJSON = mapper.writeValueAsString(supervisor);
            supervisorObject = mapper.readValue(supervisorJSON, LearningSupervisor.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LearningProcess currentLearningProcess = _learningProcessService.createLearningProcess(
                name, description,
                LocalDateTime.now(), LocalDateTime.now(),
                false, false,
                false, false,
                0.0f , 0.0f,
                0.0f, 0.0f,
                20,20,
                20,20,
                20,
                _supervisorService.getLearningSupervisorByUserName(supervisorObject != null ? supervisorObject.getUsername() : null),
                _learningProcessStatusService.getLearningProcessStatusById(0));


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
    public LearningProcess update(@PathVariable String id, @RequestBody Map<String, Object> body) {

        long learningProcessId = Long.parseLong(id);
        String name = body.get("name").toString();
        String description = body.get("description").toString();

        LocalDateTime starting_date_time = LocalDateTime.parse(body.get("starting_date_time").toString());
        LocalDateTime end_date_time = LocalDateTime.parse(body.get("end_date_time").toString());

        boolean is_cal1_available = Boolean.parseBoolean(body.get("is_cal1_available").toString());
        boolean is_cal2_available = Boolean.parseBoolean(body.get("is_cal2_available").toString());
        boolean is_cal3_available = Boolean.parseBoolean(body.get("is_cal3_available").toString());
        boolean is_calF_available = Boolean.parseBoolean(body.get("is_calF_available").toString());

        float limit_cal1 = Float.parseFloat(body.get("limit_cal1").toString());
        float limit_cal2 = Float.parseFloat(body.get("limit_cal2").toString());
        float limit_rev1 = Float.parseFloat(body.get("limit_rev1").toString());
        float limit_rev2 = Float.parseFloat(body.get("limit_rev2").toString());

        int weight_param_A = Integer.parseInt(body.get("weight_param_A").toString());
        int weight_param_B = Integer.parseInt(body.get("weight_param_B").toString());
        int weight_param_C = Integer.parseInt(body.get("weight_param_C").toString());
        int weight_param_D = Integer.parseInt(body.get("weight_param_D").toString());
        int weight_param_E = Integer.parseInt(body.get("weight_param_E").toString());

        Object supervisor = body.get("learning_supervisor");
        Object learningProcessStatus = body.get("learning_process_status");

        String supervisorJSON= null;
        String learningProcessStatusJSON = null;

        LearningSupervisor supervisorObject = null;
        LearningProcessStatus learningProcessStatusObject = null;

        try {
            supervisorJSON = mapper.writeValueAsString(supervisor);
            supervisorObject = mapper.readValue(supervisorJSON, LearningSupervisor.class);

            learningProcessStatusJSON = mapper.writeValueAsString(learningProcessStatus);
            learningProcessStatusObject = mapper.readValue(learningProcessStatusJSON, LearningProcessStatus.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                _supervisorService.getLearningSupervisorByUserName(supervisorObject != null ? supervisorObject.getUsername() : null),
                _learningProcessStatusService.getLearningProcessStatusById(learningProcessStatusObject != null ? learningProcessStatusObject.getId() : null));
    }

    @PutMapping("/api/cetpe/lprocess/usergroup/add/{id}")
    public LearningProcess updateAddUserGroup(@PathVariable String id, @RequestBody Map<String, Object> body) {

        long learningProcessId = Long.parseLong(id);

        Object userGroup = body.get("userGroupList"); //as indicated in getters and setters of the model
        String userGroupToAddJSON= null;
        List<UserGroup> userGroupToAddObject = null;

        try {
            userGroupToAddJSON = mapper.writeValueAsString(userGroup);
            //userGroupToAddObject = mapper.readValue(userGroupToAddJSON, new TypeReference<List<UserGroup>>(){});
            userGroupToAddObject = Arrays.asList(mapper.readValue(userGroupToAddJSON, UserGroup[].class)); // This is faster option

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return _learningProcessService.updateByAddingUserGroup(learningProcessId,userGroupToAddObject.get(0));
    }

    @PutMapping("/api/cetpe/lprocess/usergroup/remove/{id}")
    public LearningProcess updateRemoveUserGroup(@PathVariable String id, @RequestBody Map<String, Object> body) {

        long learningProcessId = Long.parseLong(id);

        Object userGroup = body.get("userGroupList");
        String userGroupToRemoveJSON= null;
        List<UserGroup> userGroupToRemoveObject = null;

        try {
            userGroupToRemoveJSON = mapper.writeValueAsString(userGroup);
            userGroupToRemoveObject = Arrays.asList(mapper.readValue(userGroupToRemoveJSON, UserGroup[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return _learningProcessService.updateByRemovingUserGroup(learningProcessId,userGroupToRemoveObject.get(0));
    }

    @DeleteMapping("/api/cetpe/lprocess/{id}")
    public boolean delete(@PathVariable String id) {
        long learningProcessId = Long.parseLong(id);

        return _learningProcessService.deleteLearningProcess(learningProcessId);

    }


}
