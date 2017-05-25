package net.paissad.tools.reqcoco.webapp.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.api.model.Requirement;

@Getter
@Setter
public class RequirementEntity extends Requirement implements GenericEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long              id;

}
