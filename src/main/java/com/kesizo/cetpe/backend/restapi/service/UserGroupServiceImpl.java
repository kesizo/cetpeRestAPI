package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.model.UserGroup;
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
    public List<UserGroup> getUserGroupsByLearningProcessId(long learningProcess_id) {
        return this._userGroupRepository.findByLearningProcess_id(learningProcess_id);
    }

    @Override
    public List<UserGroup> getAllUserGroups() {
        return this._userGroupRepository.findAll();
    }

    @Override
    public UserGroup createUserGroup(String name, LearningProcess learningProcess) {
        UserGroup newUserGroup = new UserGroup();

        newUserGroup.setName(name);
        newUserGroup.setLearningProcess(learningProcess);
        newUserGroup = this._userGroupRepository.save(newUserGroup);

        return newUserGroup;
    }

//    @Override
//    public UserGroup updateUserGroup(long userGroupId, String name, List<LearningStudent> studentList) {
//        UserGroup userGroupUpdatable = this._userGroupRepository.getOne(userGroupId);
//
//        if (userGroupUpdatable!=null) {
//            userGroupUpdatable.setName(name);
//            userGroupUpdatable.setLearningStudentList(studentList);
//            userGroupUpdatable = this._userGroupRepository.save(userGroupUpdatable);
//        }
//        return userGroupUpdatable;
//    }
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
    public UserGroup updateUserGroupAuthorized(long userGroupId, LearningStudent studentAuthorized) {
        UserGroup userGroupUpdatable = this._userGroupRepository.getOne(userGroupId);

        if (userGroupUpdatable!=null) {
            userGroupUpdatable.setAuthorizedStudent(studentAuthorized);
            userGroupUpdatable = this._userGroupRepository.save(userGroupUpdatable);
        }
        return userGroupUpdatable;
    }

    @Override
    public UserGroup updateByAddingLearningStudent(long userGroupId, LearningStudent newLearningStudent) {

        UserGroup userGroupUpdatable = this._userGroupRepository.getOne(userGroupId);

        if (userGroupUpdatable!=null) {
            //newLearningStudent.setLearningProcess(userGroupUpdatable);
            userGroupUpdatable.addLearningStudent(newLearningStudent);
            userGroupUpdatable = this._userGroupRepository.save(userGroupUpdatable);
        } else {
            logger.warn("No user groups available with id "+userGroupId);
            userGroupUpdatable = null;
        }
        return userGroupUpdatable;
    }

    @Override
    public UserGroup updateByRemovingLearningStudent(long userGroupId, LearningStudent toRemoveLearningStudent) {

        UserGroup userGroupUpdatable = this._userGroupRepository.getOne(userGroupId);

        if (userGroupUpdatable!=null) {
            LearningStudent existingToRemove = userGroupUpdatable.getLearningStudentList().stream()
                    .filter(student -> student.getUsername().equals(toRemoveLearningStudent.getUsername()))
                    .findAny()
                    .orElse(null);

            if (existingToRemove!=null) {

                userGroupUpdatable.getLearningStudentList().remove(existingToRemove);
                userGroupUpdatable = this._userGroupRepository.save(userGroupUpdatable);
            }
            else {
                logger.warn("The student id requested to remove doesn't exist in the user group with id "+userGroupId);
            }

        } else {
            logger.warn("No user group available with id "+userGroupId);
            userGroupUpdatable = null;
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