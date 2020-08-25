package com.kesizo.cetpe.backend.restapi.model;

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
@Table(name = "learning_student")
public class LearningStudent {

    //Logger has to be static otherwise it will considered by JPA as column
    private static Logger logger = LoggerFactory.getLogger(LearningStudent.class);

    @Id
    @Column(name="username", unique=true, nullable = false, length = 256)
    @Size(min = 3, max = 256) // it requires to have at least 3 characters
    private String username;

    @Column(name="first_name", nullable = false, length = 256)
    @Size(min = 3, max = 256)
    private String firstName;

    @Column(name="last_name", nullable = false, length = 256)
    @Size(min = 1, max = 256)
    private String lastName;

    //https://www.baeldung.com/jpa-many-to-many
    //the mappedBy must be the value of the attribute of the list from the other class
    @ManyToMany(mappedBy = "learningStudentList")
    @JsonIgnore
    private List<UserGroup> userGroupList;

    @OneToMany(mappedBy = "learningStudent")
    private List<ItemRateByStudent> itemRatesByStudent; //This is the answer to the items (N:M rel with independent entity)

    public LearningStudent(){

    }

    public LearningStudent(@Size(min = 3, max = 256) String username, @Size(min = 3, max = 256) String firstName, String lastName, List<UserGroup> userGroupList) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userGroupList = userGroupList;
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

    public List<UserGroup> getUserGroupList() {
        return userGroupList;
    }

    public void addUserGroupList(UserGroup userGroup) {
        if(this.userGroupList == null){
            this.userGroupList = new ArrayList<>();
        }
        this.userGroupList.add(userGroup);
    }

    public List<ItemRateByStudent> getItemRatesByStudent() {
        return itemRatesByStudent;
    }

    public void addItemRatesByStudent(ItemRateByStudent itemRate){
        if(this.itemRatesByStudent == null){
            this.itemRatesByStudent = new ArrayList<>();
        }
        this.itemRatesByStudent.add(itemRate);
    }

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("username",this.username);
            jsonInfo.put("firstName",this.firstName);
            jsonInfo.put("lastName",this.lastName);
            jsonInfo.put("itemRatesByStudent",this.itemRatesByStudent);
            jsonInfo.put("userGroupList",this.userGroupList);

        } catch (JSONException e) {
            logger.error("Error creating Learning Process Student JSON String representation");
            logger.error(e.getMessage());
        }


        info = jsonInfo.toString();
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningStudent that = (LearningStudent) o;
        return username.equals(that.username) &&
                firstName.equals(that.firstName) &&
                lastName.equals(that.lastName) &&
                Objects.equals(userGroupList, that.userGroupList) &&
                Objects.equals(itemRatesByStudent, that.itemRatesByStudent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, firstName, lastName, userGroupList, itemRatesByStudent);
    }
}
