package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.repository.LearningProcessStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("LearningProcessStatusService")
public class LearningProcessStatusServiceImpl implements LearningProcessStatusService {


    Logger logger = LoggerFactory.getLogger(LearningProcessServiceImpl.class);

    @Autowired
    private LearningProcessStatusRepository _learningProcessStatusRepository;

    @Override
    public LearningProcessStatus getLearningProcessStatusById(long id) {
        // IMPORTANT: difference between getOne abd findById
        // https://www.javacodemonk.com/difference-between-getone-and-findbyid-in-spring-data-jpa-3a96c3ff
        //return this._learningProcessStatusRepository.getOne(id);
        return this._learningProcessStatusRepository.findById(id).orElse(null);
    }

    @Override
    public List<LearningProcessStatus> getAllLearningProcessStatus() {
        return this._learningProcessStatusRepository.findAll();
    }

    @Override
    public LearningProcessStatus updateLearningProcessStatus(long learningProcessStatusId, String name, String definition) {

        // IMPORTANT: difference between getOne abd findById
        // https://www.javacodemonk.com/difference-between-getone-and-findbyid-in-spring-data-jpa-3a96c3ff
        LearningProcessStatus lpsUpdatable = this._learningProcessStatusRepository.findById(learningProcessStatusId).orElse(null);

        if (lpsUpdatable!=null) {
            lpsUpdatable.setName(name);
            lpsUpdatable.setDescription(definition);
            lpsUpdatable = this._learningProcessStatusRepository.save(lpsUpdatable);
        }
        return lpsUpdatable;
    }

    @Override
    public boolean deleteLearningProcessStatus(long learningProcessStatusId) {
        boolean isDeleted = true;
        try {
            this._learningProcessStatusRepository.deleteById(learningProcessStatusId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;
    }
}