package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.model.LearningSupervisor;

import java.util.List;

public interface LearningSupervisorService {

    LearningSupervisor getLearningSupervisorById(long id);

    LearningSupervisor getLearningSupervisorByUserName(String username);

    List<LearningSupervisor> getAllLearningSupervisors();

    LearningSupervisor createLearningSupervisor(String username, String firstName, String lastName);

    LearningSupervisor updateLearningSupervisor(long learningSupervisorId,
                                                String username,
                                                String firstName,
                                                String lastName);

    boolean deleteLearningSupervisor(long learningSupervisorId);
}
