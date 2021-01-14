package com.kesizo.cetpe.backend.restapi.app.service;


import com.kesizo.cetpe.backend.restapi.app.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.app.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.app.model.RubricType;

import java.time.LocalDateTime;
import java.util.List;

public interface AssessmentRubricService {

    AssessmentRubric getAssessmentRubricById(long id);

    List<AssessmentRubric> getAllAssessmentRubrics();

    List<AssessmentRubric> getAssessmentRubricsByLearningProcessId(long learningProcess_id);

    AssessmentRubric createAssessmentRubric(String title,
                                            LocalDateTime starting_date_time,
                                            LocalDateTime end_date_time,
                                            int rank,
                                            boolean enable,
                                            RubricType rubricType,
                                            LearningProcess learningProcess);

    AssessmentRubric updateAssessmentRubric(long assessmentRubricId,
                                           String title,
                                           LocalDateTime starting_date_time,
                                           LocalDateTime end_date_time,
                                           int rank,
                                           boolean enable,
                                           RubricType rubricType,
                                           LearningProcess learningProcess);

    boolean deleteAssessmentRubric(long assessmentRubricId);
}
