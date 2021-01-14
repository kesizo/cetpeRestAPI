package com.kesizo.cetpe.backend.restapi.app.service;

import com.kesizo.cetpe.backend.restapi.app.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.app.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.app.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.app.model.UserGroup;

import java.time.LocalDateTime;
import java.util.List;

public interface LearningProcessService {

    LearningProcess getLearningProcessById(long id);

    List<LearningProcess> getAllLearningProcess();


    List<LearningProcess> getLearningProcessBySupervisorUsername(String username);

    List<LearningProcess> getLearningProcessByStudentUsername(String studentUsername);

    LearningProcess createLearningProcess(String name,
                                          String description,
                                          LocalDateTime starting_date_time,
                                          LocalDateTime end_date_time,
                                          Boolean is_cal1_available,
                                          Boolean is_cal2_available,
                                          Boolean is_cal3_available,
                                          Boolean is_calF_available,
                                          Float limit_cal1,
                                          Float limit_cal2,
                                          Float limit_rev1,
                                          Float limit_rev2,
                                          int weight_param_A,
                                          int weight_param_B,
                                          int weight_param_C,
                                          int weight_param_D,
                                          int weight_param_E,
                                          LearningSupervisor supervisor,
                                          LearningProcessStatus status);

    LearningProcess updateLearningProcess(long learningProcessId,
                                          String name,
                                          String description,
                                          LocalDateTime starting_date_time,
                                          LocalDateTime end_date_time,
                                          Boolean is_cal1_available,
                                          Boolean is_cal2_available,
                                          Boolean is_cal3_available,
                                          Boolean is_calF_available,
                                          Float limit_cal1,
                                          Float limit_cal2,
                                          Float limit_rev1,
                                          Float limit_rev2,
                                          int weight_param_A,
                                          int weight_param_B,
                                          int weight_param_C,
                                          int weight_param_D,
                                          int weight_param_E,
                                          LearningSupervisor supervisor,
                                          LearningProcessStatus status);


    LearningProcess updateByAddingUserGroup(long learningProcessId, UserGroup newUserGroup);

    LearningProcess updateByRemovingUserGroup(long learningProcessId, UserGroup newUserGroup);

    boolean deleteLearningProcess(long learningProcessId);
}
