package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated")
public class UserController {

    @Autowired
    private UserDao userDao;

    public UserController() {}

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "user/id/{username}", method = RequestMethod.GET)
    public Long getUserIDByUserName(@PathVariable String username) {
        return userDao.findIdByUsername(username);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "user/{username}", method = RequestMethod.GET)
    public User getUserByUserName(@PathVariable String username) {
        return userDao.findByUsername(username);
    }



}
