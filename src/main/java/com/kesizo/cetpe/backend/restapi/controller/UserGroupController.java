package com.kesizo.cetpe.backend.restapi.controller;

import com.kesizo.cetpe.backend.restapi.model.UserGroup;
import com.kesizo.cetpe.backend.restapi.service.LearningProcessService;
import com.kesizo.cetpe.backend.restapi.service.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class UserGroupController {

    private Logger logger = LoggerFactory.getLogger(UserGroupController.class);

    @Autowired
    private UserGroupService _userGroupService;

    @Autowired
    private LearningProcessService _learningProcessService;

    @RequestMapping(value = "/api/cetpe/group", method = RequestMethod.GET)
    public List<UserGroup> userGroupsIndex(){

        return _userGroupService.getAllUserGroups();
    }


    @RequestMapping(value = "/api/cetpe/group/{id}", method = RequestMethod.GET)
    public UserGroup userGroupById(@PathVariable String id){
        long userGroupId = Long.parseLong(id);
        return _userGroupService.getUserGroupById(userGroupId);
    }

    //http://localhost:8083/api/cetpe/lprocess/group?id_lprocess=1
    @GetMapping("/api/cetpe/lprocess/group")
    @ResponseBody
    public List<UserGroup> userGroupsByLearningProcessId(@RequestParam(required = false) String id_lprocess)
    {
        List<UserGroup> userGroupList = new ArrayList<>();
        if (null==id_lprocess) {

            logger.warn("problem retrieving user groups by lprocess_id because it is null");
        }
        else {

            userGroupList = _userGroupService.getUserGroupsByLearningProcessId(Long.parseLong(id_lprocess));
        }

        return userGroupList;
    }


    @PostMapping("/api/cetpe/lprocess/group")
    public UserGroup create(@RequestBody Map<String, String> body) {

        String name = body.get("name");
        long learningProcess_id = Long.parseLong(body.get("learningProcess_id"));

        return _userGroupService.createUserGroup(name,_learningProcessService.getLearningProcessById(learningProcess_id));
    }

    @PutMapping("/api/cetpe/lprocess/group/{id}")
    public UserGroup update(@PathVariable String id, @RequestBody Map<String, String> body) {

        long userGroupId = Long.parseLong(id);
        String name = body.get("name");

        return _userGroupService.updateUserGroup(userGroupId, name);
    }

    @DeleteMapping("/api/cetpe/lprocess/group/{id}")
    public boolean delete(@PathVariable String id) {

        long userGroupId = Long.parseLong(id);

        return _userGroupService.deleteUserGroup(userGroupId);

    }


}
