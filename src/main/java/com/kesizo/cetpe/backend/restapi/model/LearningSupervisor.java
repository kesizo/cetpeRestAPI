package com.kesizo.cetpe.backend.restapi.model;

import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "learning_supervisor")
public class LearningSupervisor {


    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "seq_learning_supervisor_generator")
    @SequenceGenerator(name="seq_learning_supervisor_generator",
            sequenceName = "learning_supervisor_seq",
            initialValue=1,
            allocationSize=100)
    private long id;


    @Column(name="username", unique=true, nullable = false, length = 256)
    @Size(min = 3, max = 256) // it requires to have at least 3 characters
    private String username;

    @Column(name="first_name", nullable = false, length = 256)
    @Size(min = 3, max = 256)
    private String firstName;

    @Column(name="last_name", nullable = false, length = 256)
    private String lastName;

    @ManyToOne
    @JoinColumn(name="learningProcess_id", nullable=false)
    private LearningProcess learningProcess;


    public LearningSupervisor(){

    }

    public LearningSupervisor(long id, @Size(min = 3, max = 256) String username, @Size(min = 3, max = 256) String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LearningProcess getLearningProcess() { return learningProcess; }

    public void setLearningProcess(LearningProcess learningProcess) { this.learningProcess = learningProcess; }

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("username",this.username);
            jsonInfo.put("firstName",this.firstName);
            jsonInfo.put("lastName",this.lastName);
            jsonInfo.put("learningProcess",this.learningProcess);
        } catch (JSONException e) {
            e.getStackTrace();
        }

        info = jsonInfo.toString();
        return info;
    }
}
