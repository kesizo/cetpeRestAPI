package com.kesizo.cetpe.backend.restapi.service;


import com.kesizo.cetpe.backend.restapi.model.CetpeUser;

import java.util.List;

public interface CetpeUserService {

    CetpeUser getCetpeUserById(long id);

    List<CetpeUser> getAllCetpeUser();


    CetpeUser createCetpeUser(String name, String password);


    CetpeUser updateCetpeUser(long cetpeUserId, String name, String password);

    boolean deleteCetpeUser(long cetpeUserId);
}
