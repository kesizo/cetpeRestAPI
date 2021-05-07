package com.kesizo.cetpe.backend.restapi.app.repository;

import com.kesizo.cetpe.backend.restapi.app.model.ItemRubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Now that we have the database ready, dependencies installed,
 * connection properties provided, the next thing is to create a
 * class that talks to the database. This class is commonly referred to as,
 * a Repository.
 */

@Repository("itemRubricRepository")
public interface ItemRubricRepository extends JpaRepository<ItemRubric, Long> {

    List<ItemRubric> findByAssessmentRubric_id(long assessmenRubric_id);
}


