package com.kesizo.cetpe.backend.restapi.app.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "learning_supervisor")
public class LearningSupervisor {

    //Logger has to be static otherwise it will considered by JPA as column
    private static Logger logger = LoggerFactory.getLogger(LearningSupervisor.class);


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
            logger.error("Error creating Learning Process Supervisor JSON String representation");
            logger.error(e.getMessage());
        }

        info = jsonInfo.toString();
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningSupervisor that = (LearningSupervisor) o;
        return username.equals(that.username) &&
                firstName.equals(that.firstName) &&
                lastName.equals(that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, firstName, lastName);
    }
}
