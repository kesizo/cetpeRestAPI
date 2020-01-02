package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.model.UserGroup;
import com.kesizo.cetpe.backend.restapi.repository.LearningStudentRepository;
import com.kesizo.cetpe.backend.restapi.repository.UserGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("UserGroupService")
public class UserGroupServiceImpl implements UserGroupService {


    Logger logger = LoggerFactory.getLogger(UserGroupServiceImpl.class);

    @Autowired
    private UserGroupRepository _userGroupRepository;

    @Override
    public UserGroup getUserGroupById(long id) {
        return this._userGroupRepository.getOne(id);
    }

    @Override
    public UserGroup getUserGroupByLearningProcessId(long learningProcess_id) {
        return this._userGroupRepository.findByLearningProcess_id(learningProcess_id);
    }

    @Override
    public List<UserGroup> getAllUserGroups() {
        return this._userGroupRepository.findAll();
    }

    @Override
    public UserGroup createUserGroup(String name) {
        UserGroup newUserGroup = new UserGroup();

        newUserGroup.setName(name);
        newUserGroup = this._userGroupRepository.save(newUserGroup);

        return newUserGroup;
    }

    @Override
    public UserGroup updateUserGroup(long userGroupId, String name) {
        UserGroup userGroupUpdatable = this._userGroupRepository.getOne(userGroupId);

        if (userGroupUpdatable!=null) {
            userGroupUpdatable.setName(name);
            userGroupUpdatable = this._userGroupRepository.save(userGroupUpdatable);
        }
        return userGroupUpdatable;
    }

    @Override
    public boolean deleteUserGroup(long userGroupId) {
        boolean isDeleted = true;

        try {
            this._userGroupRepository.deleteById(userGroupId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;
    }
}