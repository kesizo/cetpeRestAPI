package com.kesizo.cetpe.backend.restapi.service;


import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.ItemRubric;
import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.RubricType;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRubricService {

    ItemRubric getItemRubricById(long id);

    List<ItemRubric> getAllItemRubrics();

    List<ItemRubric> getItemRubricsByRubricId(long rubricId);

    List<ItemRubric> getItemRubricsByLearningProcessId(long lprocessId);

    ItemRubric createItemRubric(String goal_description,
                                 int weight,
                                 AssessmentRubric assessmentRubric_id);

    ItemRubric updateItemRubric(long itemRubricsId,
                                 String goal_description,
                                 int weight,
                                 AssessmentRubric assessmentRubric_id);

    boolean deleteItemRubric(long itemRubricId);
}