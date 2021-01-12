package com.kesizo.cetpe.backend.restapi.service;


import com.kesizo.cetpe.backend.restapi.model.CetpeUser_OLD;
import com.kesizo.cetpe.backend.restapi.repository.CetpeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("CetpeUserService")
public class CetpeUserServiceImpl implements CetpeUserService {

    @Autowired
    private CetpeUserRepository _cetpeUserRepository;

    @Override
    public CetpeUser_OLD getCetpeUserById(long id) {
        return this._cetpeUserRepository.getOne(id);
    }

    @Override
    public List<CetpeUser_OLD> getAllCetpeUser() {
        return this._cetpeUserRepository.findAll();
    }

    @Override
    public CetpeUser_OLD createCetpeUser(String name, String password) {
        CetpeUser_OLD newCetpeUser = new CetpeUser_OLD();
        newCetpeUser.setName(name);
        newCetpeUser.setPassword(password);
        newCetpeUser = this._cetpeUserRepository.save(newCetpeUser);
        return newCetpeUser;
    }

    @Override
    public CetpeUser_OLD updateCetpeUser(long cetpeUserId, String name, String password) {
        CetpeUser_OLD dpsUpdatable = this._cetpeUserRepository.getOne(cetpeUserId);

        if (dpsUpdatable!=null) {
            dpsUpdatable.setName(name);
            dpsUpdatable.setPassword(password);
            dpsUpdatable = this._cetpeUserRepository.save(dpsUpdatable);
        }
        return dpsUpdatable;
    }

    @Override
    public boolean deleteCetpeUser(long cepteUserId) {
        boolean isDeleted = true;
        try {
            this._cetpeUserRepository.deleteById(cepteUserId);
        } catch (Exception e) {
            isDeleted = false;
        }
        return isDeleted;
    }
}