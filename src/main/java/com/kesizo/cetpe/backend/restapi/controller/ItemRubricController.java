package com.kesizo.cetpe.backend.restapi.controller;

import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.ItemRubric;
import com.kesizo.cetpe.backend.restapi.service.AssessmentRubricService;
import com.kesizo.cetpe.backend.restapi.service.ItemRubricService;
import com.kesizo.cetpe.backend.restapi.service.LearningProcessService;
import com.kesizo.cetpe.backend.restapi.service.RubricTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kesizo.cetpe.backend.restapi.util.Constants.DATE_FORMATTER;

@RestController
public class ItemRubricController {

    private Logger logger = LoggerFactory.getLogger(ItemRubricController.class);

    @Autowired
    private AssessmentRubricService _assessmentRubricService;

    @Autowired
    private ItemRubricService _itemRubricService;

    @RequestMapping(value = "/api/cetpe/lprocess/rubric/item/{id}", method = RequestMethod.GET)
    public ItemRubric itemRubricById(@PathVariable String id){

        return _itemRubricService.getItemRubricById(Long.parseLong(id));
    }

    //http://localhost:8083/api/cetpe/lprocess/rubric/item?id_lprocess=1
    @GetMapping("/api/cetpe/lprocess/rubric/item")
    @ResponseBody
    public List<ItemRubric> itemRubricByLearningProcessIdOrRubricId(@RequestParam(required = false) String id_lprocess,
                                                                       @RequestParam(required = false) String id_rubric)
    {
        List<ItemRubric> itemList;
        if (null==id_lprocess && null==id_rubric) {

            logger.warn("problem retrieving items, lprocess_id and id_rubric are null");
            itemList = _itemRubricService.getAllItemRubrics();
        }
        else if (null==id_rubric) {

            itemList = _itemRubricService.getItemRubricsByLearningProcessId(Long.parseLong(id_lprocess));
        }
        else if (null == id_lprocess) {
            itemList = _itemRubricService.getItemRubricsByRubricId(Long.parseLong(id_rubric));
        }
        else {
            logger.warn("problem retrieving items, lprocess_id and id_rubric are provided, but only 1 is accepted. lprocess_id will be ignored");
            itemList = _itemRubricService.getItemRubricsByRubricId(Long.parseLong(id_rubric));
        }

        return itemList;
    }

    @PostMapping("/api/cetpe/lprocess/rubric/item")
    public ItemRubric create(@RequestBody Map<String, String> body) {

        String description = body.get("description");
        int weight = Integer.parseInt(body.get("weight"));
        long assessmentRubric_id = Long.parseLong(body.get("assessmentRubric_id"));

        return _itemRubricService.createItemRubric(description, weight,
                                                    _assessmentRubricService.getAssessmentRubricById(assessmentRubric_id));
    }

    @PutMapping("/api/cetpe/lprocess/rubric/item/{id}")
    public ItemRubric update(@PathVariable String id, @RequestBody Map<String, String> body) {

        long itemRubricId = Long.parseLong(id);

        String description = body.get("description");
        int weight = Integer.parseInt(body.get("weight"));
        long assessmentRubric_id = Long.parseLong(body.get("assessmentRubric_id"));

        return _itemRubricService.updateItemRubric(itemRubricId, description, weight,
                _assessmentRubricService.getAssessmentRubricById(assessmentRubric_id));

    }

    @DeleteMapping("/api/cetpe/lprocess/rubric/item/{id}")
    public boolean delete(@PathVariable String id) {
        long itemRubricId = Long.parseLong(id);

        return _itemRubricService.deleteItemRubric(itemRubricId);

    }


}
