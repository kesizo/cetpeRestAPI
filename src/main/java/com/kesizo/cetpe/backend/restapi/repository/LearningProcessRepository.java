package com.kesizo.cetpe.backend.restapi.repository;

import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Now that we have the database ready, dependencies installed,
 * connection properties provided, the next thing is to create a
 * class that talks to the database. This class is commonly referred to as,
 * a Repository.
 */

@Repository("learningProcessRepository")
public interface LearningProcessRepository extends JpaRepository<LearningProcess, Long> {

    @Query(value = "SELECT * FROM learning_process t WHERE t.supervisor_id = :supervisor_username", nativeQuery=true)
    List<LearningProcess> findBySupervisorUsername(@Param("supervisor_username") String supervisor_username);

    //SELECT u.id, user_group_id, ugs.learning_student_id, lp.id, lp.description, lp.end_date_time, lp.is_cal1_available, lp.is_cal2_available, lp.is_cal3_available,
    //		lp.is_calf_available, lp.limit_cal1, lp.limit_cal2, lp.limit_rev1, lp.limit_rev2, lp.name,
    //		lp.starting_date_time, lp.weight_param_a, lp.weight_param_b, lp.weight_param_c, lp.weight_param_d,
    //		lp.weight_param_e, lp.status_id, lp.supervisor_id
    //	FROM (((rel_user_group_learning_student ugs
    //		INNER JOIN learning_student s ON ugs.learning_student_id = s.username AND s.username = 'usernameStudent1')
    //		INNER JOIN user_group u ON u.id = ugs.user_group_id)
    //		INNER JOIN learning_process lp on lp.id = u.learning_process_id)
    //  GROUP BY lp.id
    // NOTE: Included the following fields ( u.id, user_group_id, ugs.learning_student_id) in the select when debugging on an pgAdmin or any other DataBase IDE
    @Query(value = "SELECT lp.id, lp.description, lp.end_date_time, lp.is_cal1_available, lp.is_cal2_available, lp.is_cal3_available," +
            "lp.is_calf_available, lp.limit_cal1, lp.limit_cal2, lp.limit_rev1, lp.limit_rev2, lp.name," +
            "lp.starting_date_time, lp.weight_param_a, lp.weight_param_b, lp.weight_param_c, lp.weight_param_d," +
            "lp.weight_param_e, lp.status_id, lp.supervisor_id\n" +
            "FROM (((rel_user_group_learning_student ugs \n" +
            "INNER JOIN learning_student s ON ugs.learning_student_id = s.username AND s.username = :student_username)" +
            "INNER JOIN user_group u ON u.id = ugs.user_group_id)" +
            "INNER JOIN learning_process lp on lp.id = u.learning_process_id)" +
            "GROUP BY lp.id", nativeQuery = true)
    List<LearningProcess> findByStudentEnrolledUserName(@Param("student_username") String student_username);

}


