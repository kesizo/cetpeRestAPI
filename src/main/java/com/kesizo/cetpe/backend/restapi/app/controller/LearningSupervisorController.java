package com.kesizo.cetpe.backend.restapi.app.controller;

import com.kesizo.cetpe.backend.restapi.app.model.LearningSupervisor;
import com.kesizo.cetpe.backend.restapi.app.service.LearningSupervisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class LearningSupervisorController {

    private static final Logger logger = LoggerFactory.getLogger(LearningSupervisorController.class);

    @Autowired
    private LearningSupervisorService _learningSupervisorService;

    @RequestMapping(value = "/api/cetpe/lsupervisor", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public List<LearningSupervisor> learningSupervisorsIndex(){
        return _learningSupervisorService.getAllLearningSupervisors();
    }

    @RequestMapping(value = "/api/cetpe/lsupervisor/{username}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public LearningSupervisor learningSupervisorsByUsername(@PathVariable String username){

        final LearningSupervisor currentSupervisor;

        currentSupervisor = _learningSupervisorService.getLearningSupervisorByUserName(username);

        return currentSupervisor;
    }


    @PostMapping("/api/cetpe/lsupervisor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM')")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningSupervisor create(@RequestBody Map<String, Object> body) {

        try {
            String username = body.get("username").toString();
            String firstName = body.get("firstName").toString();
            String lastName = body.get("lastName").toString();

            return _learningSupervisorService.createLearningSupervisor(username,firstName, lastName);

        } catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when creating supervisor");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when creating supervisor", np);
        } catch (
            TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }

    }

    @PutMapping("/api/cetpe/lsupervisor/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public LearningSupervisor update(@PathVariable String username, @RequestBody Map<String, Object> body) {

        try {
            String firstName = body.get("firstName").toString();
            String lastName = body.get("lastName").toString();

            return _learningSupervisorService.updateLearningSupervisor(username,firstName, lastName);

        }
        catch (NullPointerException np) {
            logger.warn("Error mandatory parameter null provided when creating supervisor");
            logger.warn(np.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error mandatory parameter null provided when creating supervisor", np);
        }
        catch (TransactionSystemException cve) {
            logger.warn(cve.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error Bean Validation failed", cve);
        }
    }

    @DeleteMapping("/api/cetpe/lsupervisor/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(@PathVariable String username) {

        if (username==null || username.isEmpty() || username.length()<3 || username.length()>256) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error supervisor username is not valid");
        }


        return _learningSupervisorService.deleteLearningSupervisor(username);

    }

}
