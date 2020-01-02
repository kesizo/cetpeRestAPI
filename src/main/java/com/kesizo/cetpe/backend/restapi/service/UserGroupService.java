package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.model.UserGroup;

import java.util.List;

public interface UserGroupService {

    UserGroup getUserGroupById(long id);

    UserGroup getUserGroupByLearningProcessId(long learningProcess_id);

    List<UserGroup> getAllUserGroups();

    UserGroup createUserGroup(String name);

    UserGroup updateUserGroup(long userGroupId, String name);

    boolean deleteUserGroup(long userGroupId);
}
