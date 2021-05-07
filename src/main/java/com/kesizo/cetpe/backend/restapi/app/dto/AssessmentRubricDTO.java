package com.kesizo.cetpe.backend.restapi.app.dto;

import com.kesizo.cetpe.backend.restapi.app.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.app.model.RubricType;

import java.time.LocalDateTime;
import java.util.Objects;

public class AssessmentRubricDTO {

    private long id;
    private String title;
    private LocalDateTime starting_date_time;
    private LocalDateTime end_date_time;
    private int rank;
    private boolean enabled;
    private long learningProcess_id;
    private RubricType rubricType;

    public AssessmentRubricDTO(long id, String title, LocalDateTime starting_date_time, LocalDateTime end_date_time, int rank, boolean enabled, long learningProcess_id, RubricType rubricType) {
        this.id = id;
        this.title = title;
        this.starting_date_time = starting_date_time;
        this.end_date_time = end_date_time;
        this.rank = rank;
        this.enabled = enabled;
        this.learningProcess_id = learningProcess_id;
        this.rubricType = rubricType;
    }

    public AssessmentRubricDTO(AssessmentRubric entityObject) {
        this.id = entityObject.getId();
        this.title = entityObject.getTitle();
        this.starting_date_time = entityObject.getStarting_date_time();
        this.end_date_time = entityObject.getEnd_date_time();
        this.rank = entityObject.getRank();
        this.enabled = entityObject.isEnabled();
        this.learningProcess_id = entityObject.getLearningProcess().getId();
        this.rubricType = entityObject.getRubricType();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStarting_date_time() {
        return starting_date_time;
    }

    public void setStarting_date_time(LocalDateTime starting_date_time) {
        this.starting_date_time = starting_date_time;
    }

    public LocalDateTime getEnd_date_time() {
        return end_date_time;
    }

    public void setEnd_date_time(LocalDateTime end_date_time) {
        this.end_date_time = end_date_time;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getLearningProcess_id() {
        return learningProcess_id;
    }

    public void setLearningProcess_id(long learningProcess_id) {
        this.learningProcess_id = learningProcess_id;
    }

    public RubricType getRubricType() {
        return rubricType;
    }

    public void setRubricType(RubricType rubricType) {
        this.rubricType = rubricType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentRubricDTO that = (AssessmentRubricDTO) o;
        return id == that.id && rank == that.rank && enabled == that.enabled && learningProcess_id == that.learningProcess_id && title.equals(that.title) && starting_date_time.equals(that.starting_date_time) && end_date_time.equals(that.end_date_time) && rubricType.equals(that.rubricType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, starting_date_time, end_date_time, rank, enabled, learningProcess_id, rubricType);
    }

    @Override
    public String toString() {
        return "AssessmentRubricDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", starting_date_time=" + starting_date_time +
                ", end_date_time=" + end_date_time +
                ", rank=" + rank +
                ", enabled=" + enabled +
                ", learningProcess_id=" + learningProcess_id +
                ", rubricType=" + rubricType +
                '}';
    }
}

