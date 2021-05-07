package com.kesizo.cetpe.backend.restapi.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.app.dto.ItemRateByStudentDTO;
import com.kesizo.cetpe.backend.restapi.app.model.ItemRateByStudent;
import com.kesizo.cetpe.backend.restapi.app.model.ItemRubric;
import com.kesizo.cetpe.backend.restapi.app.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.app.model.UserGroup;
import com.kesizo.cetpe.backend.restapi.app.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ItemRateByStudentController {

    private static final Logger logger = LoggerFactory.getLogger(ItemRateByStudentController.class);

    @Autowired
    private AssessmentRubricService _assessmentRubricService;

    @Autowired
    private ItemRubricService _itemRubricService;

    @Autowired
    private ItemRateByStudentService _itemRateService;

    @Autowired
    private LearningProcessService _learningProcessService;

    @Autowired
    private LearningStudentService _learningStudentService;

    @Autowired
    private UserGroupService _userGroupService;

    @Autowired
    private ObjectMapper mapper; // Used to convert Objects to/from JSON


    @RequestMapping(value = "/api/cetpe/rate", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public List<ItemRateByStudent> itemRateAll(){

        return _itemRateService.getAllItemRates();
    }

    @RequestMapping(value = "/api/cetpe/rate/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ItemRateByStudent itemRateById(@PathVariable String id){

        try {
            return _itemRateService.getItemRateByStudentById(Long.parseLong(id));

        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when getting Rate ");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when getting Rate", np);
        } catch (NumberFormatException nfe) {
            logger.warn("Error Rate is a mandatory parameter and it has been provided as null when getting Rate ");
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Error Rate mandatory parameter null provided when getting Rate", nfe);
        }
    }


    @GetMapping("/api/cetpe/rates/item/{id_item}")
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public List<ItemRateByStudentDTO> ratesByItemId(@PathVariable String id_item)
    {
        try {

            return _itemRateService.getItemRatesByItemId(Long.parseLong(id_item)).stream()
                    .map(entity -> new ItemRateByStudentDTO(entity))
                    .collect(Collectors.toList());
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when getting Rate by ItemID ");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when getting Rate by ItemID", np);
        } catch (NumberFormatException nfe) {
            logger.warn("Error Rate is a mandatory parameter and it has been provided as null when getting Rate by ItemID");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Rate mandatory parameter null provided when getting Rate by ItemID", nfe);
        }

    }

    //http://localhost:8083/api/cetpe/lprocess/rubric/item/student/rate?id_lprocess=1&id_rubric=1
    @GetMapping("/api/cetpe/lprocess/rubric/item/student/rate")
    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public List<ItemRateByStudentDTO> ratesByLearningProcessIdAndRubricId(@RequestParam(required = true) String id_lprocess,
                                                                       @RequestParam(required = true) String id_rubric,
                                                                       @RequestParam(required = false) String id_learningSudent)
    {
        List<ItemRateByStudent> rateList =  new ArrayList<>();
        long lprocess_id;
        long rubric_id;

        try {
            lprocess_id = Long.parseLong(id_lprocess);
            rubric_id = Long.parseLong(id_rubric);
        }
        catch (NumberFormatException nfe) {
            logger.warn("problem retrieving rates, lprocess_id and id_rubric are not valid ids");
            logger.warn(nfe.getMessage());
            return rateList.stream().map(entity -> new ItemRateByStudentDTO(entity)).collect(Collectors.toList());
        }

        if (null==id_lprocess || null==id_rubric) {

            logger.warn("problem retrieving rates, lprocess_id and id_rubric are null");
            return rateList.stream().map(entity -> new ItemRateByStudentDTO(entity)).collect(Collectors.toList());
        }
        else if (id_learningSudent==null) {

            this._assessmentRubricService.getAssessmentRubricsByLearningProcessId(lprocess_id).forEach(rubric -> {
                    if (rubric.getId()==rubric_id) {
                        _itemRubricService.getItemRubricsByRubricId(rubric_id).forEach(itemRubric -> {
                                    rateList.addAll(_itemRateService.getItemRatesByItemId(itemRubric.getId()) );
                        });
                    }
            });
        }
        else {
            this._assessmentRubricService.getAssessmentRubricsByLearningProcessId(lprocess_id).forEach(rubric -> {
                if (rubric.getId()==rubric_id) {
                    _itemRubricService.getItemRubricsByRubricId(rubric_id).forEach(itemRubric -> {
                        rateList.addAll(_itemRateService.getItemRatesByItemId(itemRubric.getId())
                                            .stream()
                                            .filter(rate -> rate.getLearningStudent().getUsername().equals(id_learningSudent))
                                            .collect(Collectors.toList()) );
                    });
                }
            });
        }


        return rateList.stream().map(entity -> new ItemRateByStudentDTO(entity)).collect(Collectors.toList());
       // return rateList;
    }

    @PostMapping("/api/cetpe/rate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ItemRateByStudent create(@RequestBody Map<String, Object> body) {

        try {
            String justification = body.get("justification").toString();
            int rate = Integer.parseInt(body.get("rate").toString());

            Object learningStudent = body.get("learningStudent");
            Object itemRubric = body.get("itemRubric");
            Object targetStudent = body.get("targetStudent");
            Object targetUserGroup = body.get("targetUserGroup");


            if (targetStudent==null && targetUserGroup==null)
            {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error TargetStudent and targetUserGroup cannot be both null in the JSON provided when creating rate");
            }

            if (targetStudent!=null && targetUserGroup!=null)
            {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error: One of the parameters TargetStudent or targetUserGroup must be null in the JSON provided when creating rate");
            }

            String learningStudentJSON = null;
            LearningStudent learningStudentObject = null;

            String itemRubricJSON = null;
            ItemRubric itemRubricObject = null;

            String targetStudentJSON = null;
            LearningStudent targetStudentObject = null;

            String targetUserGroupJSON = null;
            UserGroup targetUserGroupObject = null;

            try {

                learningStudentJSON = mapper.writeValueAsString(learningStudent);
                learningStudentObject = mapper.readValue(learningStudentJSON, LearningStudent.class);

                itemRubricJSON = mapper.writeValueAsString(itemRubric);
                itemRubricObject = mapper.readValue(itemRubricJSON, ItemRubric.class);

                if (rate<0){
                    logger.warn("Error rate value cannot be negative when creating rate");
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Error rate value cannot be negative when creating rate");

                } else if (rate > itemRubricObject.getAssessmentRubric().getRank()) {
                    logger.warn("Error rate value cannot be higher than rubric rank");
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Error rate value cannot be higher than rubric rank");
                }

                targetStudentJSON = mapper.writeValueAsString(targetStudent);
                targetStudentObject = mapper.readValue(targetStudentJSON, LearningStudent.class);

                targetUserGroupJSON = mapper.writeValueAsString(targetUserGroup);
                targetUserGroupObject = mapper.readValue(targetUserGroupJSON, UserGroup.class);

            } catch (JsonProcessingException e) {
                logger.warn("Error when converting passed mandatory parameter to JSON provided when creating rate");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting passed mandatory parameter to JSON provided when creating rate", e);
            } catch (IOException e) {
                logger.warn("Error when converting JSON parameter to model object when creating rate");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting JSON parameter to model object when creating rate", e);
            }



            return _itemRateService.createItemRateByStudent(justification, rate,
                                                        learningStudentObject,
                                                        //_learningStudentService.getLearningStudentByUserName(learningStudentObject.getUsername()),
                                                        itemRubricObject,
                                                        //_itemRubricService.getItemRubricById(itemRubricObject.getId()),
                                                        targetStudentObject,
                                                        //_learningStudentService.getLearningStudentByUserName(targetStudentObject.getUsername()),
                                                        targetUserGroupObject);
                                                        //_userGroupService.getUserGroupById(targetUserGroupObject.getId()));

        } catch (NumberFormatException nfe) {
            logger.warn("Error Rate is a mandatory parameter and it has been provided as null when creating Rate ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Rate mandatory parameter null provided when creating Rate", nfe);
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when creating Rate ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when creating Rate", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @PutMapping("/api/cetpe/rate/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ItemRateByStudent update(@PathVariable String id, @RequestBody Map<String, Object> body) {

        try {
            long itemRateByStudentId = Long.parseLong(id);

            String justification = body.get("justification").toString();
            int rate = Integer.parseInt(body.get("rate").toString());

            Object learningStudent = body.get("learningStudent");
            Object itemRubric = body.get("itemRubric");
            Object targetStudent = body.get("targetStudent");
            Object targetUserGroup = body.get("targetUserGroup");

            if (targetStudent==null && targetUserGroup==null)
            {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error TargetStudent and targetUserGroup cannot be both null in the JSON provided when updating rate");
            }

            if (targetStudent!=null && targetUserGroup!=null)
            {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error: One of the parameters TargetStudent or targetUserGroup must be null in the JSON provided when updating rate");
            }

            String learningStudentJSON = null;
            LearningStudent learningStudentObject = null;

            String itemRubricJSON = null;
            ItemRubric itemRubricObject = null;

            String targetStudentJSON = null;
            LearningStudent targetStudentObject = null;

            String targetUserGroupJSON = null;
            UserGroup targetUserGroupObject = null;

            try {

                learningStudentJSON = mapper.writeValueAsString(learningStudent);
                learningStudentObject = mapper.readValue(learningStudentJSON, LearningStudent.class);

                itemRubricJSON = mapper.writeValueAsString(itemRubric);
                itemRubricObject = mapper.readValue(itemRubricJSON, ItemRubric.class);

                if (rate<0){
                    logger.warn("Error rate value cannot be negative when creating rate");
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Error rate value cannot be negative when updating rate");

                } else if (rate > itemRubricObject.getAssessmentRubric().getRank()) {
                    logger.warn("Error rate value cannot be higher than rubric rank");
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Error rate value cannot be higher than rubric rank");
                }

                targetStudentJSON = mapper.writeValueAsString(targetStudent);
                targetStudentObject = mapper.readValue(targetStudentJSON, LearningStudent.class);

                targetUserGroupJSON = mapper.writeValueAsString(targetUserGroup);
                targetUserGroupObject = mapper.readValue(targetUserGroupJSON, UserGroup.class);

            } catch (JsonProcessingException e) {
                logger.warn("Error when converting passed mandatory parameter to JSON provided when updating rate");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting passed mandatory parameter to JSON provided when updating rate", e);
            } catch (IOException e) {
                logger.warn("Error when converting JSON parameter to model object when updating rate");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting JSON parameter to model object when updating rate", e);
            }

            return _itemRateService.updateItemRateByStudent(itemRateByStudentId, justification, rate,
                    //_learningStudentService.getLearningStudentByUserName(learningStudent_username),
                    learningStudentObject,
                    //_itemRubricService.getItemRubricById(itemRubric_id),
                    itemRubricObject,
                    //_learningStudentService.getLearningStudentByUserName(learningStudent_username),
                    targetStudentObject,
                    //_userGroupService.getUserGroupById(targetUserGroup_id));
                    targetUserGroupObject);

        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when updating ItemRateByStudent ");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when updating ItemRateByStudent", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @DeleteMapping("/api/cetpe/rate/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public boolean delete(@PathVariable String id) {

        long itemRateId;
        try {
            itemRateId = Long.parseLong(id);
        } catch (NumberFormatException nfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error id parameter is not numeric", nfe);
        }

        return _itemRateService.deleteItemRateByStudent(itemRateId);

    }
}