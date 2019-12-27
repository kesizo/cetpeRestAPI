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
        return this._learningProcessStatusRepository.getOne(id);
    }

    @Override
    public List<LearningProcessStatus> getAllLearningProcessStatus() {
        return this._learningProcessStatusRepository.findAll();
    }

    @Override
    public LearningProcessStatus updateLearningProcessStatus(long learningProcessStatusId, String name, String definition) {

        LearningProcessStatus lpsUpdatable = this._learningProcessStatusRepository.getOne(learningProcessStatusId);

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

      /*
    @Override
    public LearningProcessStatus createLearningProcessStatusStatus(String name, String definition) {
        LearningProcessStatus newLearningProcessStatus = new LearningProcessStatus();
        newLearningProcessStatus.setName(name);
        newLearningProcessStatus.setDescription(definition);
        newLearningProcessStatus = this._learningProcessStatusRepository.save(newLearningProcessStatus);
        return newLearningProcessStatus;
    }*/
}