package com.kesizo.cetpe.backend.restapi.app.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "assessment_rubric")
@JsonIdentityInfo( //It solves recursive problems with JSON -> https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = AssessmentRubric.class
)
public class AssessmentRubric {
    //Logger has to be static otherwise it will considered by JPA as column
    private static Logger logger = LoggerFactory.getLogger(AssessmentRubric.class);

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


    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default true")
    private boolean enabled;

    //@ManyToOne(fetch = FetchType.LAZY, cascade= CascadeType.ALL) //in this way it removes the type when removing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubricType_id")
    private RubricType rubricType;

    @ManyToOne
    @JoinColumn(name="learningProcess_id", nullable=false)
    private LearningProcess learningProcess;

    // This cascade and orphanRemoval means that all children rubrics will be removed when the learning process is removed
    @OneToMany(mappedBy = "assessmentRubric",fetch = FetchType.LAZY,  cascade = CascadeType.ALL, orphanRemoval = true) // https://www.baeldung.com/delete-with-hibernate
    private List<ItemRubric> item_list;

    public AssessmentRubric() {

    }

    public AssessmentRubric(long id,
                            String title,
                            LocalDateTime starting_date_time,
                            LocalDateTime end_date_time,
                            @Min(1) int rank,
                            boolean enabled,
                            RubricType rubricType,
                            LearningProcess learningProcess) {
        this.id = id;
        this.title = title;
        this.starting_date_time = starting_date_time;
        this.end_date_time = end_date_time;
        this.rank = rank;
        this.enabled = enabled;
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

    public List<ItemRubric> getItemList() { return item_list; }

    public void setItemList(List<ItemRubric> item_list) {
        this.item_list = item_list;
    }

//    public void addItemRubric(ItemRubric itemRubric){
//        if (this.item_list == null) {
//            this.item_list = new ArrayList<>();
//        }
//        this.item_list.add(itemRubric);
//    }
//
//    public void removeItemList(ItemRubric itemRubric) {
//        if (this.item_list == null) {
//            this.item_list = new ArrayList<>();
//        }
//        if (this.item_list.contains(itemRubric)) {
//            this.item_list.remove(itemRubric);
//        }
//    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {

        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id", this.id);
            jsonInfo.put("title", this.title);
            jsonInfo.put("starting_date_time", this.starting_date_time);
            jsonInfo.put("end_date_time", this.end_date_time);
            jsonInfo.put("rank", this.rank);
            jsonInfo.put("enabled", this.enabled);
            jsonInfo.put("rubricType", this.rubricType);
            jsonInfo.put("learningProcess", this.learningProcess);
            jsonInfo.put("item_list", this.item_list);

        } catch (JSONException e) {
            logger.error("Error creating Rubric JSON String representation");
            logger.error(e.getMessage());
        }

        info = jsonInfo.toString();
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentRubric that = (AssessmentRubric) o;
        return id == that.id &&
                rank == that.rank &&
                enabled == that.enabled &&
                title.equals(that.title) &&
                starting_date_time.equals(that.starting_date_time) &&
                end_date_time.equals(that.end_date_time) &&
                rubricType.equals(that.rubricType) &&
                learningProcess.equals(that.learningProcess) &&
                Objects.equals(item_list, that.item_list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, starting_date_time, end_date_time, rank, enabled, rubricType, learningProcess, item_list);
    }

}

