package com.kesizo.cetpe.backend.restapi.model;

import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "item_rubric")
public class ItemRubric {

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

    @ManyToOne
    @JoinColumn(name="assessmentRubric_id", nullable=false)
    private AssessmentRubric assessmentRubric;

    @OneToMany(mappedBy = "itemRubric")
    private Set<ItemRateByStudent> itemRatesByStudent; //This is the answer to the items (N:M rel)

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


    public Set<ItemRateByStudent> getItemRatesByStudent() {
        return itemRatesByStudent;
    }

    public void addItemRatesByStudent(ItemRateByStudent itemRatesByStudent) {

        if(this.itemRatesByStudent == null){
            this.itemRatesByStudent = new HashSet<>();
        }
        this.itemRatesByStudent.add(itemRatesByStudent);
    }

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        jsonInfo.put("id",this.id);
        jsonInfo.put("description",this.description);
        jsonInfo.put("weight",this.weight);
        jsonInfo.put("assessmentRubric",this.assessmentRubric);
        jsonInfo.put("itemRatesByStudent",this.itemRatesByStudent);
        info = jsonInfo.toString();
        return info;
    }
}

