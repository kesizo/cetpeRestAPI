package com.kesizo.cetpe.backend.restapi.service;


import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.ItemRubric;

import java.util.List;

public interface ItemRubricService {

    ItemRubric getItemRubricById(long id);

    List<ItemRubric> getAllItemRubrics();

    List<ItemRubric> getItemRubricsByRubricId(long rubricId);

    List<ItemRubric> getItemRubricsByLearningProcessId(long lprocessId);

    ItemRubric createItemRubric(String goal_description,
                                 int weight,
                                 AssessmentRubric assessmentRubric);

    ItemRubric updateItemRubric(long itemRubricsId,
                                 String goal_description,
                                 int weight,
                                 AssessmentRubric assessmentRubric);

    boolean deleteItemRubric(long itemRubricId);
}