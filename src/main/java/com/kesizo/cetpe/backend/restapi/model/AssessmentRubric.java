package com.kesizo.cetpe.backend.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "assessment_rubric")
public class AssessmentRubric {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "seq_assessment_rubric_generator")
    @SequenceGenerator(name="seq_assessment_rubric_generator",
            sequenceName = "assessment_rubric_seq",
            initialValue=1,
            allocationSize=100)
    private long id;

    @Column(name = "title", nullable = false, length = 256)
    @Size(min = 3, max = 256) // it requires to have at least 3 characters
    private String title;

    @Column(name = "starting_date_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime starting_date_time;

    @Column(name = "end_date_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime end_date_time;

    //It indicates the rank of values of the current rubric items. From 1 to the value stored in the attribute
    @Column(name = "rank", nullable = false)
    @Min(1)
    private int rank;

    //@ManyToOne(fetch = FetchType.LAZY, cascade= CascadeType.ALL) //in this way it removes the type when removing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubricType_id")
    private RubricType rubricType;

    @ManyToOne
    @JoinColumn(name="learningProcess_id", nullable=false)
    private LearningProcess learningProcess;

    // This cascade and orphanRemoval means that all children rubrics will be removed when the learning process is removed
    @OneToMany(mappedBy = "assessmentRubric", cascade = CascadeType.ALL, orphanRemoval = true) // https://www.baeldung.com/delete-with-hibernate
    @JsonIgnore // https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
    private List<ItemRubric> itemList;

    public AssessmentRubric() {

    }

    public AssessmentRubric(long id,
                            String title,
                            LocalDateTime starting_date_time,
                            LocalDateTime end_date_time,
                            @Min(1) int rank,
                            RubricType rubricType,
                            LearningProcess learningProcess) {
        this.id = id;
        this.title = title;
        this.starting_date_time = starting_date_time;
        this.end_date_time = end_date_time;
        this.rank = rank;
        this.rubricType = rubricType;
        this.learningProcess = learningProcess;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStarting_date_time() { return starting_date_time; }

    public void setStarting_date_time(LocalDateTime starting_date_time) { this.starting_date_time = starting_date_time; }

    public LocalDateTime getEnd_date_time() { return end_date_time; }

    public void setEnd_date_time(LocalDateTime end_date_time) { this.end_date_time = end_date_time; }

    public int getRank() { return rank; }

    public void setRank(int rank) { this.rank = rank; }

    public RubricType getRubricType() { return rubricType; }

    public void setRubricType(RubricType rubricType) { this.rubricType = rubricType; }

    public LearningProcess getLearningProcess() { return learningProcess; }

    public void setLearningProcess(LearningProcess learningProcess) { this.learningProcess = learningProcess; }

    public List<ItemRubric> getItemList() { return itemList; }

    public void addItemRubric(ItemRubric itemRubric){
        if(this.itemList == null){
            this.itemList = new ArrayList<>();
        }
        this.itemList.add(itemRubric);
    }


    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        jsonInfo.put("id",this.id);
        jsonInfo.put("title",this.title);
        jsonInfo.put("starting_date_time",this.starting_date_time);
        jsonInfo.put("end_date_time",this.end_date_time);
        jsonInfo.put("rank",this.rank);
        jsonInfo.put("rubricType",this.rubricType);
        jsonInfo.put("learningProcess",this.learningProcess);

        info = jsonInfo.toString();
        return info;
    }
}

