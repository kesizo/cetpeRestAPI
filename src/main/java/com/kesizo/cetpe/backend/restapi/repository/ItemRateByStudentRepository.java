package com.kesizo.cetpe.backend.restapi.repository;

import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.ItemRateByStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Now that we have the database ready, dependencies installed,
 * connection properties provided, the next thing is to create a
 * class that talks to the database. This class is commonly referred to as,
 * a Repository.
 */

@Repository("itemRateByStudentRepository")
public interface ItemRateByStudentRepository extends JpaRepository<ItemRateByStudent, Long> {

    List<ItemRateByStudent> findByLearningStudent_id(long learningStudent_id);

    List<ItemRateByStudent> findByTargetStudent_id(long targetStudent_id);

    List<ItemRateByStudent> findByItemRubric_id(long itemRubric_id);

    List<ItemRateByStudent> findByTargetUserGroup_id(long userGroup_id);

}

