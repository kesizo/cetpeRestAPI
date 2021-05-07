package com.kesizo.cetpe.backend.restapi.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kesizo.cetpe.backend.restapi.app.model.AssessmentRubric;

public class ItemRubricDTO {
    private Long id;
    private String description;
    private int weight;

    @JsonIgnore
    private AssessmentRubric assessmentRubric;

    public ItemRubricDTO(Long id, String description, int weight, AssessmentRubric assessmentRubric) {
        this.id = id;
        this.description = description;
        this.weight = weight;
        this.assessmentRubric = assessmentRubric;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public AssessmentRubric getAssessmentRubric() {
        return assessmentRubric;
    }

    public void setAssessmentRubric(AssessmentRubric assessmentRubric) {
        this.assessmentRubric = assessmentRubric;
    }

    @Override
    public String toString() {
        return "ItemRubric{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", weight=" + weight +
                ", assessmentRubric=" + assessmentRubric +
                '}';
    }
}
