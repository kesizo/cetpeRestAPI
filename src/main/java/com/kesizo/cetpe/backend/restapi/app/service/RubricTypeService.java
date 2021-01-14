package com.kesizo.cetpe.backend.restapi.app.service;

import com.kesizo.cetpe.backend.restapi.app.model.RubricType;

import java.util.List;

public interface RubricTypeService {

    RubricType getRubricTypeById(long id);

    List<RubricType> getAllRubricTypes();

    RubricType updateRubricType(long rubricTypeId, String type);

    boolean deleteRubricType(long rubricTypeId);
}
