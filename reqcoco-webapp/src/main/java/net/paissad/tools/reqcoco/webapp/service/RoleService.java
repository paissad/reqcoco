package net.paissad.tools.reqcoco.webapp.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import net.paissad.tools.reqcoco.webapp.model.Role;
import net.paissad.tools.reqcoco.webapp.model.RoleType;

public interface RoleService extends PagingAndSortingRepository<Role, Long> {

    Role findByType(final RoleType type);

}
