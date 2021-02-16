package com.kesizo.cetpe.backend.restapi.app.repository;

import com.kesizo.cetpe.backend.restapi.app.model.LearningProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Now that we have the database ready, dependencies installed,
 * connection properties provided, the next thing is to create a
 * class that talks to the database. This class is commonly referred to as,
 * a Repository.
 */

@Repository("learningProcessStatusRepository")
public interface LearningProcessStatusRepository extends JpaRepository<LearningProcessStatus, Long> {

}

