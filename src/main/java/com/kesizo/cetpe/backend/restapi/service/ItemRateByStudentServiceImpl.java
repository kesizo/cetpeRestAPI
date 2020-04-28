package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.*;
import com.kesizo.cetpe.backend.restapi.repository.ItemRateByStudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("ItemRateByStudentService")
public class ItemRateByStudentServiceImpl implements ItemRateByStudentService
{

    Logger logger = LoggerFactory.getLogger(ItemRateByStudentServiceImpl.class);

    @Autowired
    private ItemRateByStudentRepository _itemRateByStudentRepository;

    @Override
    public ItemRateByStudent getItemRateByStudentById(long id) {
        return this._itemRateByStudentRepository.getOne(id);
    }

    @Override
    public List<ItemRateByStudent> getAllItemRates() {
        return this._itemRateByStudentRepository.findAll();
    }

    @Override
    public List<ItemRateByStudent> getItemRatesByLearningStudentId(String learningStudent_username) {
        return this._itemRateByStudentRepository.findByLearningStudent_username(learningStudent_username);
    }

    @Override
    public List<ItemRateByStudent> getItemRatesByTargetStudentId(String learningStudent_username) {
        return this._itemRateByStudentRepository.findByTargetStudent_username(learningStudent_username);
    }

    @Override
    public List<ItemRateByStudent> getItemRatesByTargetUserGroupId(long userGroupId) {
        return this._itemRateByStudentRepository.findByTargetUserGroup_id(userGroupId);
    }

    @Override
    public List<ItemRateByStudent> getItemRatesByItemId(long itemId) {
        return this._itemRateByStudentRepository.findByItemRubric_id(itemId);
    }

    @Override
    public ItemRateByStudent createItemRateByStudent(String justification, int rate, LearningStudent learningStudent, ItemRubric itemRubric,
                                                     LearningStudent targetStudent, UserGroup targetUserGroup) {

        ItemRateByStudent newItemRateByStudent = new ItemRateByStudent();

        newItemRateByStudent.setJustification(justification);
        newItemRateByStudent.setRate(rate);
        newItemRateByStudent.setLearningStudent(learningStudent);
        newItemRateByStudent.setItemRubric(itemRubric);

        newItemRateByStudent.setTargetStudent(targetStudent);
        newItemRateByStudent.setTargetUserGroup(targetUserGroup);

        newItemRateByStudent = this._itemRateByStudentRepository.save(newItemRateByStudent);
        return newItemRateByStudent;
    }

    @Override
    public ItemRateByStudent updateItemRateByStudent(long itemRateByStudentId, String justification, int rate, LearningStudent learningStudent, ItemRubric itemRubric,
                                                     LearningStudent targetStudent, UserGroup targetUserGroup) {

        ItemRateByStudent itemRateByStudentUpdatable = this._itemRateByStudentRepository.getOne(itemRateByStudentId);

        if (itemRateByStudentUpdatable!=null) {
            itemRateByStudentUpdatable.setJustification(justification);
            itemRateByStudentUpdatable.setRate(rate);
            itemRateByStudentUpdatable.setLearningStudent(learningStudent);
            itemRateByStudentUpdatable.setItemRubric(itemRubric);

            itemRateByStudentUpdatable.setTargetStudent(targetStudent);
            itemRateByStudentUpdatable.setTargetUserGroup(targetUserGroup);
        }
        else {
            logger.warn("No itemRateByStudent available with id "+itemRateByStudentId);
            itemRateByStudentUpdatable = null;
        }
        return itemRateByStudentUpdatable;
    }

    @Override
    public boolean deleteItemRateByStudent(long itemRateByStudentId) {

        boolean isDeleted = true;
        try {
            this._itemRateByStudentRepository.deleteById(itemRateByStudentId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;


    }
}