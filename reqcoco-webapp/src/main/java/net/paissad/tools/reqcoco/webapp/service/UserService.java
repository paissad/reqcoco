package net.paissad.tools.reqcoco.webapp.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import net.paissad.tools.reqcoco.webapp.model.User;

public interface UserService extends PagingAndSortingRepository<User, Long> {

    User findByLogin(final String login);

    User findByEmail(final String email);

}
