package com.kesizo.cetpe.backend.restapi.app.service;

import com.kesizo.cetpe.backend.restapi.app.model.ItemRateByStudent;
import com.kesizo.cetpe.backend.restapi.app.model.ItemRubric;
import com.kesizo.cetpe.backend.restapi.app.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.app.model.UserGroup;

import java.util.List;

public interface ItemRateByStudentService {

    ItemRateByStudent getItemRateByStudentById(long id);

    List<ItemRateByStudent> getAllItemRates();

    List<ItemRateByStudent> getItemRatesByLearningStudentId(String learningStudent_username);

    List<ItemRateByStudent> getItemRatesByTargetStudentId(String learningStudent_username);

    List<ItemRateByStudent> getItemRatesByTargetUserGroupId(long id);

    List<ItemRateByStudent> getItemRatesByItemId(long id);

    ItemRateByStudent createItemRateByStudent(String justification, int rate, LearningStudent learningStudent, ItemRubric itemRubric, LearningStudent targetStudent, UserGroup targetUserGroup);

    ItemRateByStudent updateItemRateByStudent(long itemRateByStudentId, String justification, int rate, LearningStudent learningStudent, ItemRubric itemRubric, LearningStudent targetStudent, UserGroup targetUserGroup);

    boolean deleteItemRateByStudent(long itemRateByStudentId);
}
