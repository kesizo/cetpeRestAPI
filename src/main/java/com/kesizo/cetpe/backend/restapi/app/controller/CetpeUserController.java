package com.kesizo.cetpe.backend.restapi.app.controller;

import com.kesizo.cetpe.backend.restapi.app.model.CetpeUser_OLD;
import com.kesizo.cetpe.backend.restapi.app.service.CetpeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CetpeUserController {

    @Autowired
    private CetpeUserService _cetpeUserService;

    @RequestMapping(value = "/api/cetpe/lprocess/user", method = RequestMethod.GET)
    public List<CetpeUser_OLD> cetpeLearningUserIndex(){
        return _cetpeUserService.getAllCetpeUser();
    }

    //Operations with the blogs. Retrieve (GET), Update (PUT), Remove (DELETE)
    @GetMapping("/api/cetpe/lprocess/user/{id}")
    public CetpeUser_OLD show(@PathVariable String id){
        long cetpeUserId = Long.parseLong(id);

        return _cetpeUserService.getCetpeUserById(cetpeUserId);
    }

    @PostMapping("/api/cetpe/lprocess/user")
    public CetpeUser_OLD create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String password = body.get("password");

        return _cetpeUserService.createCetpeUser(name, password);


    }

    @DeleteMapping("/api/cetpe/lprocess/user/{id}")
    public boolean delete(@PathVariable String id) {
        long delphiUserId = Long.parseLong(id);

        return _cetpeUserService.deleteCetpeUser(delphiUserId);

    }

    @PutMapping("/api/cetpe/lprocess/user/{id}")
    public CetpeUser_OLD update(@PathVariable String id, @RequestBody Map<String, String> body) {
        long cetpeUserId = Long.parseLong(id);
        String cetpeUserName = body.get("name");
        String cetpeUserPassword = body.get("password");

        return _cetpeUserService.updateCetpeUser(cetpeUserId, cetpeUserName, cetpeUserPassword);
    }

}