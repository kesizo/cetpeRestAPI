package com.kesizo.cetpe.backend.restapi.app.service;

import com.kesizo.cetpe.backend.restapi.app.model.LearningSupervisor;

import java.util.List;

public interface LearningSupervisorService {


    LearningSupervisor getLearningSupervisorByUserName(String username);

    List<LearningSupervisor> getAllLearningSupervisors();

    LearningSupervisor createLearningSupervisor(String username, String firstName, String lastName);

    LearningSupervisor updateLearningSupervisor(
                                                String username,
                                                String firstName,
                                                String lastName);

    boolean deleteLearningSupervisor(String username);
}
