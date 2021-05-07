package com.kesizo.cetpe.backend.restapi.app.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "item_rubric")
@JsonIdentityInfo( //It solves recursive problems with JSON -> https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"//,
       // scope = ItemRubric.class // This is needed otherwise if nested JSON objects have the same id it will fails
)
public class ItemRubric {

    //Logger has to be static otherwise it will considered by JPA as column
    private static Logger logger = LoggerFactory.getLogger(ItemRubric.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "seq_item_rubric_generator")
    @SequenceGenerator(name="seq_item_rubric_generator",
            sequenceName = "item_rubric_seq",
            initialValue=1,
            allocationSize=100)
    private long id;

    @Column(name = "description", nullable = false, length =  2048) //Characters
    private String description;


    @Column(name = "weight", nullable = false)
    @Min(1)
    private int weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assessmentRubric_id", nullable=false)
    private AssessmentRubric assessmentRubric;

//    @OneToMany(mappedBy = "itemRubric", fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "itemRubric", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRateByStudent> itemRatesByStudent; //This is the answer to the items (N:M rel)

    public ItemRubric() {

    }

    public ItemRubric(long id, String description, @Min(1) int weight, AssessmentRubric assessmentRubric) {
        this.id = id;
        this.description = description;
        this.weight = weight;
        this.assessmentRubric = assessmentRubric;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
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


    public List<ItemRateByStudent> getItemRatesByStudent() {
        return itemRatesByStudent;
    }

    public void setItemRatesByStudent(List<ItemRateByStudent> itemRatesByStudent) {

        if(this.itemRatesByStudent == null){
            this.itemRatesByStudent = new ArrayList<>();
        }
        else {
            this.itemRatesByStudent = getItemRatesByStudent();
        }
    }

//    public void addItemRatesByStudent(ItemRateByStudent itemRatesByStudent) {
//
//        if(this.itemRatesByStudent == null){
//            this.itemRatesByStudent = new ArrayList<>();
//        }
//        else {
//            this.itemRatesByStudent.add(itemRatesByStudent);
//        }
//    }

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("description",this.description);
            jsonInfo.put("weight",this.weight);
            jsonInfo.put("assessmentRubric",this.assessmentRubric);
            jsonInfo.put("itemRatesByStudent",this.itemRatesByStudent);
        } catch (JSONException e) {
            logger.error("Error creating ItemRubric JSON String representation");
            logger.error(e.getMessage());
        }

        info = jsonInfo.toString();
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRubric that = (ItemRubric) o;
        return id == that.id &&
                weight == that.weight &&
                description.equals(that.description) &&
                assessmentRubric.equals(that.assessmentRubric) &&
                Objects.equals(itemRatesByStudent, that.itemRatesByStudent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, weight, assessmentRubric, itemRatesByStudent);
    }
}

