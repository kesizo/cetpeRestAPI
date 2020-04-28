package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.LearningProcess;
import com.kesizo.cetpe.backend.restapi.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.model.UserGroup;

import java.util.List;

public interface UserGroupService {

    UserGroup getUserGroupById(long id);

    List<UserGroup> getUserGroupsByLearningProcessId(long learningProcess_id);

    List<UserGroup> getAllUserGroups();

    UserGroup createUserGroup(String name, LearningProcess learningProcess);

    UserGroup updateUserGroup(long userGroupId, String name);

    UserGroup updateUserGroupAuthorized(long userGroupId, LearningStudent studentAuthorized);

    UserGroup updateByAddingLearningStudent(long userGroupId, LearningStudent newLearningStudent);

    UserGroup updateByRemovingLearningStudent(long userGroupId, LearningStudent toRemoveLearningStudent);

    boolean deleteUserGroup(long userGroupId);
}
