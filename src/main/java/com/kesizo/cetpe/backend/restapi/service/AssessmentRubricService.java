package com.kesizo.cetpe.backend.restapi.service;


import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.RubricType;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

public interface AssessmentRubricService {

    AssessmentRubric getAssessmentRubricById(long id);

    List<AssessmentRubric> getAllAssessmentRubrics();

    AssessmentRubric createAssessmentRubric(String title,
                                            LocalDateTime starting_date_time,
                                            LocalDateTime end_date_time,
                                            int rank,
                                            RubricType rubricType,
                                            LearningProcess learningProcess);

    AssessmentRubric updateAssessmentRubric(long assessmentRubricId,
                                           String title,
                                           LocalDateTime starting_date_time,
                                           LocalDateTime end_date_time,
                                           int rank,
                                           RubricType rubricType,
                                           LearningProcess learningProcess);

    boolean deleteAssessmentRubric(long assessmentRubricId);
}
