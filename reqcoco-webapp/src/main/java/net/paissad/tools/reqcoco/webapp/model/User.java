package net.paissad.tools.reqcoco.webapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

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

    @NotNull
    @Column(unique = true, nullable = false, updatable = false, length = 20)
    @ApiModelProperty(notes = "The login of the user", required = true)
    private String            login;

    @NotNull
    @Column(nullable = false, length = 20)
    @ApiModelProperty(notes = "The password of the user", required = true)
    private String            password;

    @Email
    @Column(unique = true, nullable = true, length = 64)
    @ApiModelProperty(notes = "The email address of the user", required = true)
    private String            email;

    @NotNull
    @Column(nullable = true, length = 40)
    @ApiModelProperty(notes = "The complete/full name of the user", required = true)
    private String            fullName;

}
