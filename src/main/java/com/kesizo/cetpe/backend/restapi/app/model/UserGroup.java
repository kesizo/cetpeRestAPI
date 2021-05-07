package com.kesizo.cetpe.backend.restapi.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_group")
public class UserGroup {

    //Logger has to be static otherwise it will considered by JPA as column
    private static Logger logger = LoggerFactory.getLogger(UserGroup.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "seq_user_group_generator")
    @SequenceGenerator(name="seq_user_group_generator",
            sequenceName = "user_group_seq",
            initialValue=1,
            allocationSize=100)
    private long id;


    @Column(name = "name", nullable = false, length = 256)
    @Size(min = 3, max = 256)
    private String name;

    @ManyToOne
    @JoinColumn(name="learningProcess_id", nullable=false)
    @JsonIgnore
    private LearningProcess learningProcess;


    @ManyToMany
    @JoinTable(
            name = "rel_userGroup_learningStudent",
            joinColumns = @JoinColumn(name = "userGroup_id", nullable = false, referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="learningStudent_id", nullable = false, referencedColumnName = "username")
    ) //https://www.baeldung.com/jpa-many-to-many
    private List<LearningStudent> learningStudentList;

    @ManyToOne
    @JoinColumn(name="learningStudent_id", nullable=true)
    private LearningStudent authorizedStudent;


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


    public List<LearningStudent> getLearningStudentList() {
        return learningStudentList;
    }

    public void setLearningStudentList(List<LearningStudent> learningStudentList) {
        this.learningStudentList = learningStudentList;
    }

    public void addLearningStudent(LearningStudent student){
        if(this.learningStudentList == null){
            this.learningStudentList = new ArrayList<>();
        }
        this.learningStudentList.add(student);
    }

    public LearningStudent getAuthorizedStudent() {
        return authorizedStudent;
    }

    public void setAuthorizedStudent(LearningStudent authorizedStudent) {
        this.authorizedStudent = authorizedStudent;
    }

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("name",this.name);
            jsonInfo.put("learningProcess",this.learningProcess != null ? this.learningProcess : null);
            jsonInfo.put("learningStudentList",this.learningStudentList != null ? this.learningStudentList : null);
            jsonInfo.put("authorizedStudent",this.authorizedStudent != null ? this.authorizedStudent : null);

        } catch (JSONException e) {
            logger.error("Error creating User Group JSON String representation");
            logger.error(e.getMessage());
        }

        info = jsonInfo.toString();
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGroup userGroup = (UserGroup) o;
        return id == userGroup.id &&
                name.equals(userGroup.name) &&
                learningProcess.equals(userGroup.learningProcess) &&
                Objects.equals(learningStudentList, userGroup.learningStudentList) &&
                Objects.equals(authorizedStudent, userGroup.authorizedStudent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, learningProcess, learningStudentList, authorizedStudent);
    }
}
