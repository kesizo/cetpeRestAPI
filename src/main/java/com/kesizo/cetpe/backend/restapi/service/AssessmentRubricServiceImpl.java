package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.RubricType;
import com.kesizo.cetpe.backend.restapi.repository.AssessmentRubricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("AssessmentRubricService")
public class AssessmentRubricServiceImpl implements AssessmentRubricService
{

    Logger logger = LoggerFactory.getLogger(AssessmentRubricServiceImpl.class);

    @Autowired
    private AssessmentRubricRepository _assessmentRubricRepository;

    @Override
    public AssessmentRubric getAssessmentRubricById(long id) {
        // IMPORTANT: difference between getOne abd findById
        // https://www.javacodemonk.com/difference-between-getone-and-findbyid-in-spring-data-jpa-3a96c3ff
        //return this._learningProcessRepository.getOne(id); //throws NestedException if not found
        return this._assessmentRubricRepository.findById(id).orElse(null);

    }

    @Override
    public List<AssessmentRubric> getAllAssessmentRubrics() {
        return this._assessmentRubricRepository.findAll();
    }

    @Override
    public List<AssessmentRubric> getAssessmentRubricsByLearningProcessId(long learningProcess_id) {
        return this._assessmentRubricRepository.findByLearningProcess_id(learningProcess_id);
    }

    @Override
    public AssessmentRubric createAssessmentRubric(String title, LocalDateTime starting_date_time, LocalDateTime end_date_time, int rank, boolean enabled, RubricType rubricType, LearningProcess learningProcess) {

        AssessmentRubric newAssessmentRubric = new AssessmentRubric();

        newAssessmentRubric.setTitle(title);
        newAssessmentRubric.setStarting_date_time(starting_date_time);
        newAssessmentRubric.setEnd_date_time(end_date_time);
        newAssessmentRubric.setRank(rank);
        newAssessmentRubric.setEnabled(enabled);
        newAssessmentRubric.setRubricType(rubricType);
        newAssessmentRubric.setLearningProcess(learningProcess);

        newAssessmentRubric = this._assessmentRubricRepository.save(newAssessmentRubric);
        return newAssessmentRubric;
    }

    @Override
    public AssessmentRubric updateAssessmentRubric(long assessmentRubricId, String title, LocalDateTime starting_date_time, LocalDateTime end_date_time, int rank, boolean enabled, RubricType rubricType, LearningProcess learningProcess) {

        AssessmentRubric assessmentUpdatable = this._assessmentRubricRepository.getOne(assessmentRubricId);

        if (assessmentUpdatable!=null) {
            assessmentUpdatable.setTitle(title);
            assessmentUpdatable.setStarting_date_time(starting_date_time);
            assessmentUpdatable.setEnd_date_time(end_date_time);
            assessmentUpdatable.setRank(rank);
            assessmentUpdatable.setEnabled(enabled);
            assessmentUpdatable.setRubricType(rubricType);
            assessmentUpdatable.setLearningProcess(learningProcess);
            assessmentUpdatable = this._assessmentRubricRepository.save(assessmentUpdatable);
        }
        else {
            logger.warn("No assessment rubric available with id "+assessmentRubricId);
            assessmentUpdatable = null;
        }
        return assessmentUpdatable;

    }

    @Override
    public boolean deleteAssessmentRubric(long assessmentRubricId) {

        boolean isDeleted = true;
        try {
            this._assessmentRubricRepository.deleteById(assessmentRubricId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;

    }
}