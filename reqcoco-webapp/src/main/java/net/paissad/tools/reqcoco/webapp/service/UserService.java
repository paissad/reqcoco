package net.paissad.tools.reqcoco.webapp.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import net.paissad.tools.reqcoco.webapp.model.User;

public interface UserService extends PagingAndSortingRepository<User, Long> {

    User findByName(final String name);

    User findByEmail(final String email);

}
