package net.paissad.tools.reqcoco.webapp.controller;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.paissad.tools.reqcoco.webapp.model.User;
import net.paissad.tools.reqcoco.webapp.service.UserService;

@RestController
@RequestMapping(value = "/user", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
@Api(value = "Operations pertaining to users")
public class UserController implements ReqController {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService        userService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Return the user having the specified login or email, or the list of all users if no parameter is specified")
    private Iterable<User> getUser(@RequestParam(name = "login", required = false) final String login, @RequestParam(name = "email", required = false) final String email,
            final Model model) {

        if (!StringUtils.isBlank(login)) {
            LOGGER.info("Retrieving user with login = '{}'", login);
            final User u = this.userService.findByLogin(login);
            return u != null ? Arrays.asList(new User[] { u }) : Collections.emptyList();

        } else if (!StringUtils.isBlank(email)) {
            LOGGER.info("Retrieving user with email = '{}'", email);
            final User u = this.userService.findByEmail(login);
            return u != null ? Arrays.asList(new User[] { u }) : Collections.emptyList();

        } else {
            LOGGER.info("Retrieving all users.");
            return this.userService.findAll();
        }
    }

    @ApiOperation(value = "Return the user having the specified id")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    private User getUserById(@PathVariable final Long id, final Model model) {
        LOGGER.info("Retrieving user with id = '{}'", id);
        return this.userService.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Creates a new user")
    private ResponseEntity<User> saveUser(@RequestBody final User user) {
        LOGGER.info("Creating new user (login = {}, email = {}, fullname = {})", user.getLogin(), user.getEmail(), user.getFullName());
        final User newUser = this.userService.save(user);
        LOGGER.info("User '{}' created successfully !", user.getLogin());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update the user having the specified id")
    private ResponseEntity<User> updateUser(@PathVariable final Long id, @RequestBody User user) {

        LOGGER.info("Updating user with id = '{}'", id);
        final User u = this.userService.findOne(id);
        if (u != null) {
            user.setId(id);
            final User updatedUser = this.userService.save(user);
            LOGGER.info("User '{}' updated successfully.", user.getLogin());
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);

        } else {
            LOGGER.error("Unable to update user having the id '{}' because it is not found !", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete the user having the specified id")
    private ResponseEntity<String> deleteUser(@PathVariable final Long id) {
        LOGGER.info("Deleting user with id = '{}'", id);
        this.userService.delete(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

}
