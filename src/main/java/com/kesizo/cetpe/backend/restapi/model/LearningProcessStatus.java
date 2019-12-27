package com.kesizo.cetpe.backend.restapi.model;

import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Entity
@Table(name = "learning_process_status")
public class LearningProcessStatus {

    @Id
    private long id;

    @Column(name = "name", nullable = false, length = 256) // field length in the database 256)
    @Size(min = 5, max = 256) // it requires to have at least 3 characters
    private String name;

    @Column(name = "description", nullable = false, length=1000)
    private String description;

    public LearningProcessStatus() { }

    public LearningProcessStatus(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("name",this.name);
            jsonInfo.put("description",this.description);
        } catch (JSONException e) {
            e.getStackTrace();
        }

        info = jsonInfo.toString();
        return info;
    }
}