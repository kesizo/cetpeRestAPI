package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.model.RubricType;
import com.kesizo.cetpe.backend.restapi.repository.LearningProcessStatusRepository;
import com.kesizo.cetpe.backend.restapi.repository.RubricTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("RubricTypeService")
public class RubricTypeServiceImpl implements RubricTypeService {


    Logger logger = LoggerFactory.getLogger(RubricTypeServiceImpl.class);

    @Autowired
    private RubricTypeRepository _rubricTypeRepositoryRepository;


    @Override
    public RubricType getRubricTypeById(long id) {
        return this._rubricTypeRepositoryRepository.getOne(id);
    }

    @Override
    public List<RubricType> getAllRubricTypes() {
        return this._rubricTypeRepositoryRepository.findAll();
    }

    @Override
    public RubricType updateRubricType(long rubricTypeId, String type) {
        RubricType rtUpdatable = this._rubricTypeRepositoryRepository.getOne(rubricTypeId);

        if (rtUpdatable!=null) {
            rtUpdatable.setType(type);
            rtUpdatable = this._rubricTypeRepositoryRepository.save(rtUpdatable);
        }
        return rtUpdatable;
    }

    @Override
    public boolean deleteRubricType(long rubricTypeId) {
        boolean isDeleted = true;
        try {
            this._rubricTypeRepositoryRepository.deleteById(rubricTypeId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;
    }
}