package com.kesizo.cetpe.backend.restapi.service;


import com.kesizo.cetpe.backend.restapi.model.CetpeUser_OLD;

import java.util.List;

public interface CetpeUserService {

    CetpeUser_OLD getCetpeUserById(long id);

    List<CetpeUser_OLD> getAllCetpeUser();


    CetpeUser_OLD createCetpeUser(String name, String password);


    CetpeUser_OLD updateCetpeUser(long cetpeUserId, String name, String password);

    boolean deleteCetpeUser(long cetpeUserId);
}
