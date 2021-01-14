package com.kesizo.cetpe.backend.restapi.app.service;

import com.kesizo.cetpe.backend.restapi.app.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.app.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.app.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.app.model.UserGroup;
import com.kesizo.cetpe.backend.restapi.app.repository.LearningProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("LearningProcessService")
public class LearningProcessServiceImpl implements LearningProcessService {

    private static final Logger logger = LoggerFactory.getLogger(LearningProcessServiceImpl.class);

    @Autowired
    private LearningProcessRepository _learningProcessRepository;

    @Override
    public LearningProcess getLearningProcessById(long id) {
        // IMPORTANT: difference between getOne abd findById
        // https://www.javacodemonk.com/difference-between-getone-and-findbyid-in-spring-data-jpa-3a96c3ff
        //return this._learningProcessRepository.getOne(id); //throws NestedException if not found
        return this._learningProcessRepository.findById(id).orElse(null);
    }

    @Override
    public List<LearningProcess> getAllLearningProcess() {
        return this._learningProcessRepository.findAll();
    }

    @Override
    public List<LearningProcess> getLearningProcessBySupervisorUsername(String username) {
        return this._learningProcessRepository.findBySupervisorUsername(username);
    }

    @Override
    public List<LearningProcess> getLearningProcessByStudentUsername(String studentUsername) {
        return this._learningProcessRepository.findByStudentEnrolledUserName(studentUsername);
    }

    @Override
    public LearningProcess createLearningProcess(String name,
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
                                                LearningSupervisor learning_supervisor,
                                                LearningProcessStatus learning_process_status) {

        LearningProcess newLearningProcess = new LearningProcess();

        newLearningProcess.setName(name);
        newLearningProcess.setDescription(description);
        newLearningProcess.setStarting_date_time(starting_date_time);
        newLearningProcess.setEnd_date_time(end_date_time);
        newLearningProcess.setIs_cal1_available(is_cal1_available);
        newLearningProcess.setIs_cal2_available(is_cal2_available);
        newLearningProcess.setIs_cal3_available(is_cal3_available);
        newLearningProcess.setIs_calF_available(is_calF_available);
        newLearningProcess.setLimit_cal1(limit_cal1);
        newLearningProcess.setLimit_cal2(limit_cal2);
        newLearningProcess.setLimit_rev1(limit_rev1);
        newLearningProcess.setLimit_rev2(limit_rev2);
        newLearningProcess.setWeight_param_A(weight_param_A);
        newLearningProcess.setWeight_param_B(weight_param_B);
        newLearningProcess.setWeight_param_C(weight_param_C);
        newLearningProcess.setWeight_param_D(weight_param_D);
        newLearningProcess.setWeight_param_E(weight_param_E);
        newLearningProcess.setLearning_supervisor(learning_supervisor);
        newLearningProcess.setLearning_process_status(learning_process_status);
        newLearningProcess = this._learningProcessRepository.save(newLearningProcess);
        return newLearningProcess;
    }


    @Override
    public LearningProcess updateLearningProcess(long learningProcessId,
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
                                                             LearningSupervisor learning_supervisor,
                                                             LearningProcessStatus learning_process_status) {

        LearningProcess lpUpdatable = this._learningProcessRepository.findById(learningProcessId).orElse(null);

        if (lpUpdatable!=null) {
            lpUpdatable.setName(name);
            lpUpdatable.setDescription(description);
            lpUpdatable.setStarting_date_time(starting_date_time);
            lpUpdatable.setEnd_date_time(end_date_time);
            lpUpdatable.setIs_cal1_available(is_cal1_available);
            lpUpdatable.setIs_cal2_available(is_cal2_available);
            lpUpdatable.setIs_cal3_available(is_cal3_available);
            lpUpdatable.setIs_calF_available(is_calF_available);
            lpUpdatable.setLimit_cal1(limit_cal1);
            lpUpdatable.setLimit_cal2(limit_cal2);
            lpUpdatable.setLimit_rev1(limit_rev1);
            lpUpdatable.setLimit_rev2(limit_rev2);
            lpUpdatable.setWeight_param_A(weight_param_A);
            lpUpdatable.setWeight_param_B(weight_param_B);
            lpUpdatable.setWeight_param_C(weight_param_C);
            lpUpdatable.setWeight_param_D(weight_param_D);
            lpUpdatable.setWeight_param_E(weight_param_E);
            lpUpdatable.setLearning_supervisor(learning_supervisor);
            lpUpdatable.setLearning_process_status(learning_process_status);

            lpUpdatable = this._learningProcessRepository.save(lpUpdatable);
        }
        else {
            logger.warn("No learning process available with id "+learningProcessId);
            lpUpdatable = null;
        }
        return lpUpdatable;
    }

    @Override
    public LearningProcess updateByAddingUserGroup(long learningProcessId, UserGroup newUserGroup) {

        LearningProcess lpUpdatable = this._learningProcessRepository.findById(learningProcessId).orElse(null);

        if (lpUpdatable!=null) {
            newUserGroup.setLearningProcess(lpUpdatable);
            lpUpdatable.addUserGroup(newUserGroup);
            lpUpdatable = this._learningProcessRepository.save(lpUpdatable);
        } else {
            logger.warn("No learning process available with id "+learningProcessId);
            lpUpdatable = null;
        }
        return lpUpdatable;
    }

    @Override
    public LearningProcess updateByRemovingUserGroup(long learningProcessId, UserGroup toBeRemovedUserGroup) {

        LearningProcess lpUpdatable = this._learningProcessRepository.findById(learningProcessId).orElse(null);

        if (lpUpdatable!=null) {
            UserGroup existingToRemove = lpUpdatable.getUserGroupList().stream()
                    .filter(userGroup -> userGroup.getId() == toBeRemovedUserGroup.getId())
                    .findAny()
                    .orElse(null);

            if (existingToRemove!=null) {

                lpUpdatable.getUserGroupList().remove(existingToRemove);
                lpUpdatable = this._learningProcessRepository.save(lpUpdatable);
            }
            else {
                logger.warn("The User group requested to remove doesn't exist in the process with id "+learningProcessId);
            }

        } else {
            logger.warn("No learning process available with id "+learningProcessId);
            lpUpdatable = null;
        }
        return lpUpdatable;
    }


    @Override
    public boolean deleteLearningProcess(long learningProcessId) {
        boolean isDeleted = true;
        try {
            this._learningProcessRepository.deleteById(learningProcessId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;
    }
}