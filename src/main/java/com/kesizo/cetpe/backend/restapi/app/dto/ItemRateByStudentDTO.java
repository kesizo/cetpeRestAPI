package com.kesizo.cetpe.backend.restapi.app.dto;

import com.kesizo.cetpe.backend.restapi.app.model.ItemRateByStudent;
import com.kesizo.cetpe.backend.restapi.app.model.ItemRubric;
import com.kesizo.cetpe.backend.restapi.app.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.app.model.UserGroup;


public class ItemRateByStudentDTO {

    private long id;
    private String justification;
    private int rate;
    private LearningStudent learningStudent;
    private long itemRubric_id;

    private LearningStudent targetStudent;
    private UserGroup targetUserGroup;

    public ItemRateByStudentDTO(long id, String justification, int rate, LearningStudent learningStudent, long itemRubricId, LearningStudent targetStudent, UserGroup targetUserGroup) {
        this.id = id;
        this.justification = justification;
        this.rate = rate;
        this.learningStudent = learningStudent;
        this.itemRubric_id = itemRubricId;
        this.targetStudent = targetStudent;
        this.targetUserGroup = targetUserGroup;
    }

    public ItemRateByStudentDTO(ItemRateByStudent entityObject) {
        this.id = entityObject.getId();
        this.justification = entityObject.getJustification();
        this.rate = entityObject.getRate();
        this.learningStudent = entityObject.getLearningStudent();
        this.itemRubric_id = entityObject.getItemRubric().getId();
        this.targetStudent = entityObject.getTargetStudent();
        this.targetUserGroup = entityObject.getTargetUserGroup();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public LearningStudent getLearningStudent() {
        return learningStudent;
    }

    public void setLearningStudent(LearningStudent learningStudent) {
        this.learningStudent = learningStudent;
    }

    public long getItemRubricId() {
        return itemRubric_id;
    }

    public void setItemRubricId(long itemRubricId) {
        this.itemRubric_id = itemRubricId;
    }

    public LearningStudent getTargetStudent() {
        return targetStudent;
    }

    public void setTargetStudent(LearningStudent targetStudent) {
        this.targetStudent = targetStudent;
    }

    public UserGroup getTargetUserGroup() {
        return targetUserGroup;
    }

    public void setTargetUserGroup(UserGroup targetUserGroup) {
        this.targetUserGroup = targetUserGroup;
    }

    @Override
    public String toString() {
        return "ItemRateByStudentDTO{" +
                "id=" + id +
                ", justification='" + justification + '\'' +
                ", rate=" + rate +
                ", learningStudent=" + learningStudent +
                ", itemRubric_id=" + itemRubric_id +
                ", targetStudent=" + targetStudent +
                ", targetUserGroup=" + targetUserGroup +
                '}';
    }
}
