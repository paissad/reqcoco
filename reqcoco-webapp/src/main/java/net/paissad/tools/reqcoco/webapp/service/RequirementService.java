package net.paissad.tools.reqcoco.webapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import net.paissad.tools.reqcoco.webapp.model.RequirementEntity;

@RepositoryRestResource(path = "requirements")
public interface RequirementService extends PagingAndSortingRepository<RequirementEntity, Long> {

    Page<RequirementEntity> findByName(final String name, final Pageable pageable);

    Page<RequirementEntity> findByVersion(final String version, final Pageable pageable);

    Page<RequirementEntity> findByCodeAuthor(final String codeAuthor, final Pageable pageable);

    Page<RequirementEntity> findByTestAuthor(final String testAuthor, final Pageable pageable);

}
