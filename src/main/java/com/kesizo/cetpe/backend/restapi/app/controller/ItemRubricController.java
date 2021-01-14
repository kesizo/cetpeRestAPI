package com.kesizo.cetpe.backend.restapi.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesizo.cetpe.backend.restapi.app.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.app.model.ItemRubric;
import com.kesizo.cetpe.backend.restapi.app.service.AssessmentRubricService;
import com.kesizo.cetpe.backend.restapi.app.service.ItemRubricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ItemRubricController {

    private static final Logger logger = LoggerFactory.getLogger(ItemRubricController.class);

    @Autowired
    private AssessmentRubricService _assessmentRubricService;

    @Autowired
    private ItemRubricService _itemRubricService;

    @Autowired
    private ObjectMapper mapper; // Used to convert Objects to/from JSON



    @RequestMapping(value = "/api/cetpe/item/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ItemRubric itemRubricById(@PathVariable String id) {

        try {
            return _itemRubricService.getItemRubricById(Long.parseLong(id));

        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when getting ItemRubric ");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when getting ItemRubric", np);
        } catch (NumberFormatException nfe) {
            logger.warn("Error Item Rubric is a mandatory parameter and it has been provided as null when getting Item ");
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Error Item Rubric mandatory parameter null provided when getting Item", nfe);
    }
}

    //http://localhost:8083/api/cetpe/item?id_lprocess=1
    @GetMapping("/api/cetpe/item")
    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
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

    @PostMapping("/api/cetpe/item")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRubric create(@RequestBody Map<String, Object> body) {

        try {
            String description = body.get("description").toString();
            int weight = Integer.parseInt(body.get("weight").toString());
            Object assessmentRubric = body.get("assessmentRubric");

            String assessmentRubricJSON = null;
            AssessmentRubric assessmentRubricObject = null;

            try {

                assessmentRubricJSON = mapper.writeValueAsString(assessmentRubric);
                assessmentRubricObject = mapper.readValue(assessmentRubricJSON, AssessmentRubric.class);

            } catch (JsonProcessingException e) {
                logger.warn("Error when converting passed mandatory parameter to JSON provided when creating item on rubric");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting passed mandatory parameter to JSON provided when creating item on rubric", e);
            } catch (IOException e) {
                logger.warn("Error when converting JSON parameter to model object when creating item on rubric");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting JSON parameter to model object when creating item on rubric", e);
            }

            AssessmentRubric currentRubric = _assessmentRubricService.getAssessmentRubricById(assessmentRubricObject != null ? assessmentRubricObject.getId() : null );
            return _itemRubricService.createItemRubric(description, weight,currentRubric);

        } catch (NumberFormatException nfe) {
            logger.warn("Error Assessment Rubric is a mandatory parameter and it has been provided as null when creating Item ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Assessment Rubric mandatory parameter null provided when creating Item", nfe);
        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when creating Item ");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when creating Item", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @PutMapping("/api/cetpe/item/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN')")
    public ItemRubric update(@PathVariable String id, @RequestBody Map<String,  Object> body) {

        try {
            long itemRubricId = Long.parseLong(id);

            String description = body.get("description").toString();
            int weight = Integer.parseInt(body.get("weight").toString());
            Object assessmentRubric = body.get("assessmentRubric");

            String assessmentRubricJSON = null;
            AssessmentRubric assessmentRubricObject = null;

            try {

                assessmentRubricJSON = mapper.writeValueAsString(assessmentRubric);
                //assessmentRubricObject = mapper.readValue(assessmentRubricJSON, AssessmentRubric.class);
                // This line above fails because of recursive. So the line below is the good one because AssessmentRubric has
                // been annotated with @JsonIdentityInfo
                assessmentRubricObject = mapper.readerFor(AssessmentRubric.class).readValue(assessmentRubricJSON);

            } catch (JsonProcessingException e) {
                logger.warn("Error when converting passed mandatory parameter to JSON provided when updating item on rubric");
                logger.warn(e.getMessage());
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting passed mandatory parameter to JSON provided when updating item on rubric", e);
            } catch (IOException e) {
                logger.warn("Error when converting JSON parameter to model object when updating item on rubric");
                logger.warn(e.getMessage());
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error when converting JSON parameter to model object when updating item on rubric", e);
            }


            AssessmentRubric currentRubric = _assessmentRubricService.getAssessmentRubricById(assessmentRubricObject != null ? assessmentRubricObject.getId() : null );
            return _itemRubricService.updateItemRubric(itemRubricId,description, weight,currentRubric);

        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when updating ItemRubric ");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when updating ItemRubric", np);
        } catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }

    }


    @DeleteMapping("/api/cetpe/item/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN')")
    public boolean delete(@PathVariable String id) {

        long itemRubricId;
        try {

            itemRubricId = Long.parseLong(id);
        } catch (NumberFormatException nfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error id parameter is not numeric", nfe);
        }

        return _itemRubricService.deleteItemRubric(itemRubricId);
    }


}
