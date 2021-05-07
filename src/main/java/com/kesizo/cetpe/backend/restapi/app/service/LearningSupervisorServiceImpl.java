package com.kesizo.cetpe.backend.restapi.app.service;

import com.kesizo.cetpe.backend.restapi.app.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.app.repository.LearningSupervisorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("LearningSupervisorService")
public class LearningSupervisorServiceImpl implements LearningSupervisorService {


    private static final Logger logger = LoggerFactory.getLogger(LearningSupervisorServiceImpl.class);

    @Autowired
    private LearningSupervisorRepository _learningSupervisorRepository;

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
    public LearningSupervisor updateLearningSupervisor(String username, String firstName, String lastName) {

        LearningSupervisor supervisorUpdatable = this._learningSupervisorRepository.findByUsername(username);

        if (supervisorUpdatable!=null) {
            supervisorUpdatable.setUsername(username);
            supervisorUpdatable.setFirstName(firstName);
            supervisorUpdatable.setLastName(lastName);
            supervisorUpdatable = this._learningSupervisorRepository.save(supervisorUpdatable);
        }
        return supervisorUpdatable;
    }


    @Override
    public boolean deleteLearningSupervisor(String username) {

        boolean isDeleted = true;

        try {
            this._learningSupervisorRepository.deleteByUsername(username);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;
    }

}