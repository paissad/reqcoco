package net.paissad.tools.reqcoco.webapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@ApiModel("Represents a user")
public class User implements GenericEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated product ID", readOnly = true)
    private Long              id;

    @Column(unique = true, nullable = false, updatable = false, length = 20)
    @ApiModelProperty(notes = "The login of the user", required = true)
    private String            login;

    @Column(nullable = false, length = 20)
    private String            password;

    @Column(unique = true, nullable = true, length = 64)
    private String            email;

    @Column(nullable = true, length = 40)
    private String            fullName;

}
