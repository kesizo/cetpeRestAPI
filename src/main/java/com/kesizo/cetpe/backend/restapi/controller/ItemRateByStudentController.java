package com.kesizo.cetpe.backend.restapi.controller;

import com.kesizo.cetpe.backend.restapi.model.*;
import com.kesizo.cetpe.backend.restapi.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ItemRateByStudentController {

    private Logger logger = LoggerFactory.getLogger(ItemRateByStudentController.class);

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

    @RequestMapping(value = "/api/cetpe/lprocess/rubric/item/rate/{id}", method = RequestMethod.GET)
    public ItemRateByStudent itemRateById(@PathVariable String id){

        return _itemRateService.getItemRateByStudentById(Long.parseLong(id));
    }

    //http://localhost:8083/api/cetpe/lprocess/rubric/item/rate?id_item=1
    @GetMapping("/api/cetpe/lprocess/rubric/item/rate")
    @ResponseBody
    public List<ItemRateByStudent> ratesByItemId(@RequestParam(required = true) String id_item)
    {
        List<ItemRateByStudent> rateList =  new ArrayList<>();
        if (null==id_item) {

            logger.warn("problem retrieving rates, item_id is null");
        }
        else {

            rateList = _itemRateService.getItemRatesByItemId(Long.parseLong(id_item));
        }

        return rateList;
    }

    //http://localhost:8083/api/cetpe/lprocess/rubric/item/student/rate?id_lprocess=1&id_rubric=1
    @GetMapping("/api/cetpe/lprocess/rubric/item/student/rate")
    @ResponseBody
    public List<ItemRateByStudent> ratesByLearningProcessIdAndRubricId(@RequestParam(required = true) String id_lprocess,
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
            return rateList;
        }

        if (null==id_lprocess || null==id_rubric) {

            logger.warn("problem retrieving rates, lprocess_id and id_rubric are null");
            return rateList;
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
                                            .filter(rate -> rate.getLearningStudent().equals(id_learningSudent))
                                            .collect(Collectors.toList()) );
                    });
                }
            });
        }

        return rateList;
    }

    @PostMapping("/api/cetpe/lprocess/rubric/item/rate")
    public ItemRateByStudent create(@RequestBody Map<String, String> body) {

        String justification = body.get("justification");
        int rate = Integer.parseInt(body.get("rate"));
        long learningStudent_id = Long.parseLong(body.get("learningStudent_id"));
        long itemRubric_id = Long.parseLong(body.get("itemRubric_id"));

        long targetStudent_id = body.get("targetStudent_id")!=null && !body.get("targetStudent_id").isEmpty() ?
                                            Long.parseLong(body.get("targetStudent_id"))
                                            : 0;

        long targetUserGroup_id = body.get("targetUserGroup_id")!=null && !body.get("targetUserGroup_id").isEmpty() ?
                                     Long.parseLong(body.get("targetUserGroup_id"))
                                    : 0;


        return _itemRateService.createItemRateByStudent(justification, rate,
                                                        _learningStudentService.getLearningStudentById(learningStudent_id),
                                                        _itemRubricService.getItemRubricById(itemRubric_id),
                                                        _learningStudentService.getLearningStudentById(targetStudent_id),
                                                        _userGroupService.getUserGroupById(targetUserGroup_id));
    }

    @PutMapping("/api/cetpe/lprocess/rubric/item/rate/{id}")
    public ItemRateByStudent update(@PathVariable String id, @RequestBody Map<String, String> body) {

        long itemRateByStudentId = Long.parseLong(id);

        String justification = body.get("justification");
        int rate = Integer.parseInt(body.get("rate"));
        long learningStudent_id = Long.parseLong(body.get("learningStudent_id"));
        long itemRubric_id = Long.parseLong(body.get("itemRubric_id"));

        long targetStudent_id = body.get("targetStudent_id")!=null && !body.get("targetStudent_id").isEmpty() ?
                Long.parseLong(body.get("targetStudent_id"))
                : 0;

        long targetUserGroup_id = body.get("targetUserGroup_id")!=null && !body.get("targetUserGroup_id").isEmpty() ?
                Long.parseLong(body.get("targetUserGroup_id"))
                : 0;

        return _itemRateService.updateItemRateByStudent(itemRateByStudentId,justification, rate,
                _learningStudentService.getLearningStudentById(learningStudent_id),
                _itemRubricService.getItemRubricById(itemRubric_id),
                _learningStudentService.getLearningStudentById(targetStudent_id),
                _userGroupService.getUserGroupById(targetUserGroup_id));
    }

    @DeleteMapping("/api/cetpe/lprocess/rubric/item/rate/{id}")
    public boolean delete(@PathVariable String id) {
        long itemRateId = Long.parseLong(id);

        return _itemRateService.deleteItemRateByStudent(itemRateId);

    }


}
