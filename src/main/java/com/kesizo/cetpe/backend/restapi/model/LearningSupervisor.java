package com.kesizo.cetpe.backend.restapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "learning_supervisor")
public class LearningSupervisor {


    @Id
    @Column(name="username", unique=true, nullable = false, length = 256)
    @Size(min = 3, max = 256) // it requires to have at least 3 characters
    @JsonProperty("username")
    private String username;

    @JsonProperty("firstName")
    @Column(name="first_name", nullable = false, length = 256)
    @Size(min = 3, max = 256)
    private String firstName;

    @JsonProperty("lastName")
    @Column(name="last_name", nullable = false, length = 256)
    private String lastName;

    public LearningSupervisor(){

    }

    public LearningSupervisor( @Size(min = 3, max = 256) String username, @Size(min = 3, max = 256) String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
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

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("username",this.username);
            jsonInfo.put("firstName",this.firstName);
            jsonInfo.put("lastName",this.lastName);
        } catch (JSONException e) {
            e.getStackTrace();
        }

        info = jsonInfo.toString();
        return info;
    }
}
