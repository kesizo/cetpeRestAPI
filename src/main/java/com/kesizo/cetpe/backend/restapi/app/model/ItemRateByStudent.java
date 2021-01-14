package com.kesizo.cetpe.backend.restapi.app.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Objects;

/**
 *
 * This class is created as part of the M:N relationship between ItemRubric and LearningStudent. It has been implemented
 * according to the third solution explained here: https://www.baeldung.com/jpa-many-to-many
 */
@Entity
@Table(name = "item_rate_by_student")
@JsonIdentityInfo( //It solves recursive problems with JSON -> https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = ItemRateByStudent.class // This is needed otherwise if nested JSON objects have the same id it will fails
)
public class ItemRateByStudent {


    //Logger has to be static otherwise it will considered by JPA as column
    private static Logger logger = LoggerFactory.getLogger(ItemRateByStudent.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "seq_item_rate_by_student_generator")
    @SequenceGenerator(name="seq_item_rate_by_student_generator",
            sequenceName = "item_rate_by_student_seq",
            initialValue=1,
            allocationSize=100)
    private long id;

    @Column(name = "justification", nullable = false, length =  2048) //Characters
    private String justification;

    @Column(name = "rate", nullable = false) //Characters
    private int rate;


    @ManyToOne
    @JoinColumn(name="learningStudent_id", nullable=false)
    private LearningStudent learningStudent;

    @ManyToOne
    @JoinColumn(name="itemRubric_id", nullable=false)
    private ItemRubric itemRubric;


    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="targetStudent_id", nullable = true) //this can be null
    private LearningStudent targetStudent;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="targetUserGroup_id", nullable = true) //this can be null
    private UserGroup targetUserGroup;

    public ItemRateByStudent() {

    }

    public ItemRateByStudent(long id, String justification, int rate, LearningStudent learningStudent, ItemRubric itemRubric) {
        this.id = id;
        this.justification = justification;
        this.rate = rate;
        this.learningStudent = learningStudent;
        this.itemRubric = itemRubric;
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

    public ItemRubric getItemRubric() {
        return itemRubric;
    }

    public void setItemRubric(ItemRubric itemRubric) {
        this.itemRubric = itemRubric;
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
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("rate",this.rate);
            jsonInfo.put("justification",this.justification);
            jsonInfo.put("itemRubric",this.itemRubric);
            jsonInfo.put("learningStudent",this.learningStudent);
            jsonInfo.put("targetStudent",this.targetStudent);
        } catch (JSONException e) {
            logger.error("Error creating ItemRateByStudent JSON String representation");
            logger.error(e.getMessage());
        }

        info = jsonInfo.toString();
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRateByStudent that = (ItemRateByStudent) o;
        return id == that.id &&
                rate == that.rate &&
                justification.equals(that.justification) &&
                learningStudent.equals(that.learningStudent) &&
                itemRubric.equals(that.itemRubric) &&
                Objects.equals(targetStudent, that.targetStudent) &&
                Objects.equals(targetUserGroup, that.targetUserGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, justification, rate, learningStudent, itemRubric, targetStudent, targetUserGroup);
    }
}

