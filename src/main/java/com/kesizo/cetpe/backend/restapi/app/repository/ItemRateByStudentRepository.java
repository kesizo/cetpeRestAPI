package com.kesizo.cetpe.backend.restapi.app.repository;

import com.kesizo.cetpe.backend.restapi.app.model.ItemRateByStudent;
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

    List<ItemRateByStudent> findByLearningStudent_username(String learningStudent_username);

    List<ItemRateByStudent> findByTargetStudent_username(String learningStudent_username);

    List<ItemRateByStudent> findByItemRubric_id(long itemRubric_id);

    List<ItemRateByStudent> findByTargetUserGroup_id(long userGroup_id);

}


