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
@ToString(of = { "id", "shortDescription", "version", "revision", "codeDone", "codeAuthor", "testDone", "testAuthor", "ignore" })
public class Requirement implements Serializable {

	private static final long	serialVersionUID	= 1L;

	public static final String	VERSION_UNKNOWN		= "__unknown__";

	@XmlAttribute(required = true)
	@XmlID
	private String				id;

	@XmlAttribute
	private boolean				ignore;

	@XmlElement(required = true, nillable = false)
	private String				shortDescription;

	@XmlElement(nillable = true)
	private String				fullDescription;

	@XmlAttribute(required = true)
	private String				version;

	@XmlAttribute(required = true)
	private String				revision;

	@XmlElement(required = true, nillable = false)
	private boolean				codeDone;

	@XmlElement(nillable = true)
	private String				codeAuthor;

	@XmlElement(nillable = true)
	private String				codeAuthorComment;

	@XmlElement(required = true, nillable = false)
	private boolean				testDone;

	@XmlElement(nillable = true)
	private String				testAuthor;

	@XmlElement(nillable = true)
	private String				testAuthorComment;

	@XmlElement(nillable = true)
	private String				link;

	public Requirement() {
		// default no-arg constructor
	}

}
