package com.kesizo.cetpe.backend.restapi.model;



import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "cetpe_user")
public class CetpeUser_OLD {

    @Id
    private long id;

    @Column(name = "name", nullable = false, length = 256) // field length in the database 256
    @Size(min = 3, max = 256) // it requires to have at least 3 characters
    private String name;

    @Column(name = "password", nullable = false, length = 256)
    @Size(min = 8, max = 256) // it requires to have at least 8 characters
    private String password;

    public CetpeUser_OLD() { }

    public CetpeUser_OLD(long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("name",this.name);
            jsonInfo.put("password",this.password);
        } catch (JSONException e) {
            e.getStackTrace();
        }

        info = jsonInfo.toString();
        return info;
    }
}
