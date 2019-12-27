package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;

import java.util.List;

public interface LearningProcessStatusService {

    LearningProcessStatus getLearningProcessStatusById(long id);

    List<LearningProcessStatus> getAllLearningProcessStatus();

    /*
    LearningProcessStatus createLearningProcessStatus(String name, String definition);
    */

    LearningProcessStatus updateLearningProcessStatus(long learningProcessStatusId, String name, String definition);

    boolean deleteLearningProcessStatus(long learningProcessStatusId);
}
