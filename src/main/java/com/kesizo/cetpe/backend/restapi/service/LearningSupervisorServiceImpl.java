package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.repository.LearningProcessStatusRepository;
import com.kesizo.cetpe.backend.restapi.repository.LearningSupervisorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("LearningSupervisorService")
public class LearningSupervisorServiceImpl implements LearningSupervisorService {


    Logger logger = LoggerFactory.getLogger(LearningSupervisorServiceImpl.class);

    @Autowired
    private LearningSupervisorRepository _learningSupervisorRepository;


    @Override
    public LearningSupervisor getLearningSupervisorById(long id) {
        return this._learningSupervisorRepository.getOne(id);
    }

    @Override
    public LearningSupervisor getLearningSupervisorByUserName(String username) {
        return this._learningSupervisorRepository.findByUsername(username);
    }

    @Override
    public List<LearningSupervisor> getAllLearningSupervisors() {
        return this._learningSupervisorRepository.findAll();
    }

    @Override
    public LearningSupervisor createLearningSupervisor(String username, String firstName, String lastName) {

        LearningSupervisor newLearningSupervisor = new LearningSupervisor();

        newLearningSupervisor.setUsername(username);
        newLearningSupervisor.setFirstName(firstName);
        newLearningSupervisor.setLastName(lastName);
        newLearningSupervisor = this._learningSupervisorRepository.save(newLearningSupervisor);

        return newLearningSupervisor;
    }

    @Override
    public LearningSupervisor updateLearningSupervisor(long learningSupervisorId, String username, String firstName, String lastName) {

        LearningSupervisor supervisorUpdatable = this._learningSupervisorRepository.getOne(learningSupervisorId);

        if (supervisorUpdatable!=null) {
            supervisorUpdatable.setUsername(username);
            supervisorUpdatable.setFirstName(firstName);
            supervisorUpdatable.setLastName(lastName);
            supervisorUpdatable = this._learningSupervisorRepository.save(supervisorUpdatable);
        }
        return supervisorUpdatable;
    }


    @Override
    public boolean deleteLearningSupervisor(long learningSupervisorId) {

        boolean isDeleted = true;

        try {
            this._learningSupervisorRepository.deleteById(learningSupervisorId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;
    }

}