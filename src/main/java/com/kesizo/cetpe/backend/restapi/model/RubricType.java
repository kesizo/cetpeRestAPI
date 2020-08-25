package com.kesizo.cetpe.backend.restapi.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "rubric_type")
public class RubricType {

    //Logger has to be static otherwise it will considered by JPA as column
    private static Logger logger = LoggerFactory.getLogger(RubricType.class);


    @Id
    private long id;

    @Column(name = "type", nullable = false, length = 256)
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("type",this.type);
        } catch (JSONException e) {
            logger.error("Error creating Rubric Type JSON String representation");
            logger.error(e.getMessage());
        }

        info = jsonInfo.toString();
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RubricType that = (RubricType) o;
        return id == that.id &&
                type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
