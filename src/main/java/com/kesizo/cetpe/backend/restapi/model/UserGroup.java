package com.kesizo.cetpe.backend.restapi.model;

import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "user_group")
public class UserGroup {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "seq_user_group_generator")
    @SequenceGenerator(name="seq_user_group_generator",
            sequenceName = "user_group_seq",
            initialValue=1,
            allocationSize=100)
    private long id;


    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="learningProcess_id", nullable=false)
    private LearningProcess learningProcess;

    public UserGroup() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LearningProcess getLearningProcess() {
        return learningProcess;
    }

    public void setLearningProcess(LearningProcess learningProcess) {
        this.learningProcess = learningProcess;
    }

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("name",this.name);
            jsonInfo.put("learningProcess",this.learningProcess);
        } catch (JSONException e) {
            e.getStackTrace();
        }

        info = jsonInfo.toString();
        return info;
    }
}
