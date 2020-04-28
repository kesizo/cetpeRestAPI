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

}


