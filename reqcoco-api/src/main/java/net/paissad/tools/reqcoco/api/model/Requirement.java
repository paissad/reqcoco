package net.paissad.tools.reqcoco.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "requirement")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString(of = { "id", "name", "shortDescription", "version", "codeDone", "codeAuthor", "testDone", "testAuthor" })
public class Requirement implements Serializable {

	private static final long	serialVersionUID	= 1L;

	@XmlAttribute(required = true)
	@XmlID
	private String				id;

	@XmlElement(required = true, nillable = false)
	private String				name;

	@XmlElement(required = true, nillable = false)
	private String				shortDescription;

	@XmlElement(nillable = true)
	private String				fullDescription;

	@XmlElement(required = true, nillable = false)
	private Version				version;

	@XmlElement(required = true, nillable = false)
	private boolean				codeDone;

	@XmlElement(nillable = true)
	private String				codeAuthor;

	@XmlElement(required = true, nillable = false)
	private boolean				testDone;

	@XmlElement(nillable = true)
	private String				testAuthor;

	@XmlElement(nillable = true)
	private String				link;

	public Requirement() {
		// default no-arg constructor
	}

}
