package com.kesizo.cetpe.backend.restapi.model;

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
@Table(name = "learning_process_status")
public class LearningProcessStatus {

    //Logger has to be static otherwise it will considered by JPA as column
    private static Logger logger = LoggerFactory.getLogger(LearningProcessStatus.class);

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
            logger.error("Error creating Learning Process Status JSON String representation");
            logger.error(e.getMessage());
        }

        info = jsonInfo.toString();
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningProcessStatus that = (LearningProcessStatus) o;
        return id == that.id &&
                name.equals(that.name) &&
                description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}