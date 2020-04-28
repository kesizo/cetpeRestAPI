package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningStudent;

import java.util.List;

public interface LearningStudentService {


    LearningStudent getLearningStudentByUserName(String username);

    List<LearningStudent> getAllLearningStudents();

    LearningStudent createLearningStudent(String username, String firstName, String lastName);

    LearningStudent updateLearningStudent(String username,
                                                String firstName,
                                                String lastName);

    boolean deleteLearningStudent(String username);
}