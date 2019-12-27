package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.LearningProcessStatus;
import com.kesizo.cetpe.backend.restapi.repository.LearningProcessRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("LearningProcessService")
public class LearningProcessServiceImpl implements LearningProcessService {

    Logger logger = LoggerFactory.getLogger(LearningProcessServiceImpl.class);

    @Autowired
    private LearningProcessRepository _learningProcessRepository;

    @Override
    public LearningProcess getLearningProcessById(long id) {
        return this._learningProcessRepository.getOne(id);
    }

    @Override
    public List<LearningProcess> getAllLearningProcess() {
        return this._learningProcessRepository.findAll();
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
                                                LearningProcessStatus status) {

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
        newLearningProcess.setStatus(status);

        newLearningProcess = this._learningProcessRepository.save(newLearningProcess);
        return newLearningProcess;
    }


    @Override
    public LearningProcess updateLearningProcess(long learningProcessStatusId,
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
                                                             LearningProcessStatus status) {

        LearningProcess lpUpdatable = this._learningProcessRepository.getOne(learningProcessStatusId);

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
            lpUpdatable.setStatus(status);

            lpUpdatable = this._learningProcessRepository.save(lpUpdatable);
        }
        else {
            logger.warn("No learning process available with id "+learningProcessStatusId);
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