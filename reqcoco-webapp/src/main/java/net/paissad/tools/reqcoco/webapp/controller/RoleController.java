package net.paissad.tools.reqcoco.webapp.controller;

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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.paissad.tools.reqcoco.webapp.model.Role;
import net.paissad.tools.reqcoco.webapp.service.RoleService;

@RestController
@RequestMapping(value = "/role", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
@Api(value = "Operations pertaining to roles")
public class RoleController implements ReqController {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private RoleService        roleService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List all roles")
    private Iterable<Role> getAllRoles() {

        LOGGER.info("Retrieving all roles");
        return this.roleService.findAll();
    }

    @ApiOperation(value = "Return the role having the specified id")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    private Role getRoleById(@PathVariable final Long id, final Model model) {
        LOGGER.info("Retrieving role with id = '{}'", id);
        return this.roleService.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create a role")
    private ResponseEntity<Role> add(@RequestBody final Role role) {

        LOGGER.info("Creating role (name={} , type={})", role.getName(), role.getType());
        this.roleService.save(role);
        LOGGER.info("Role '{}' created successfully !", role.getName());
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update the role having the specified id")
    private ResponseEntity<Role> updateRole(@PathVariable final Long id, @RequestBody final Role role) {

        final Role r = this.roleService.findOne(id);

        if (r != null) {
            LOGGER.info("Updating the role '{}'", role.getName());
            final Role updatedRole = this.roleService.save(role);
            LOGGER.info("Updated role '{}' successfully !", updatedRole.getName());
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);

        } else {
            LOGGER.error("No role with the id '{}' was found !!!", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete the role having the specified id")
    private ResponseEntity<String> deleteRole(@PathVariable final Long id) {

        LOGGER.info("Deleting role with id '{}'", id);
        this.roleService.delete(id);
        return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
    }

}
