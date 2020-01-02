package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.repository.LearningStudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("LearningStudentService")
public class LearningStudentServiceImpl implements LearningStudentService {


    Logger logger = LoggerFactory.getLogger(LearningStudentServiceImpl.class);

    @Autowired
    private LearningStudentRepository _learningStudentRepository;

    @Override
    public LearningStudent getLearningStudentById(long id) {
        return this._learningStudentRepository.getOne(id);
    }

    @Override
    public LearningStudent getLearningStudentByUserName(String username) {
        return this._learningStudentRepository.findByUsername(username);
    }

    @Override
    public List<LearningStudent> getAllLearningStudents() {
        return this._learningStudentRepository.findAll();
    }

    @Override
    public LearningStudent createLearningStudent(String username, String firstName, String lastName) {

        LearningStudent newLearningStudent = new LearningStudent();

        newLearningStudent.setUsername(username);
        newLearningStudent.setFirstName(firstName);
        newLearningStudent.setLastName(lastName);
        newLearningStudent = this._learningStudentRepository.save(newLearningStudent);

        return newLearningStudent;
    }

    @Override
    public LearningStudent updateLearningStudent(long learningStudentId, String username, String firstName, String lastName) {
        LearningStudent studentUpdatable = this._learningStudentRepository.getOne(learningStudentId);

        if (studentUpdatable!=null) {
            studentUpdatable.setUsername(username);
            studentUpdatable.setFirstName(firstName);
            studentUpdatable.setLastName(lastName);
            studentUpdatable = this._learningStudentRepository.save(studentUpdatable);
        }
        return studentUpdatable;
    }

    @Override
    public boolean deleteLearningStudent(long learningStudentId) {
        boolean isDeleted = true;

        try {
            this._learningStudentRepository.deleteById(learningStudentId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;
    }
}