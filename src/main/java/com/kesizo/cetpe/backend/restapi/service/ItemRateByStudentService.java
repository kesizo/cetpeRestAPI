package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.ItemRateByStudent;
import com.kesizo.cetpe.backend.restapi.model.ItemRubric;
import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.model.UserGroup;

import java.util.List;

public interface ItemRateByStudentService {

    ItemRateByStudent getItemRateByStudentById(long id);

    List<ItemRateByStudent> getAllItemRates();

    List<ItemRateByStudent> getItemRatesByLearningStudentId(long id);

    List<ItemRateByStudent> getItemRatesByTargetStudentId(long id);

    List<ItemRateByStudent> getItemRatesByTargetUserGroupId(long id);

    List<ItemRateByStudent> getItemRatesByItemId(long id);

    ItemRateByStudent createItemRateByStudent(String justification, int rate, LearningStudent learningStudent, ItemRubric itemRubric, LearningStudent targetStudent, UserGroup targetUserGroup);

    ItemRateByStudent updateItemRateByStudent(long itemRateByStudentId, String justification, int rate, LearningStudent learningStudent, ItemRubric itemRubric, LearningStudent targetStudent, UserGroup targetUserGroup);

    boolean deleteItemRateByStudent(long itemRateByStudentId);
}
