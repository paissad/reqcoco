package net.paissad.tools.reqcoco.webapp.repository;

import org.springframework.data.repository.CrudRepository;

import net.paissad.tools.reqcoco.webapp.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByName(final String name);

    User findByEmail(final String email);

}
