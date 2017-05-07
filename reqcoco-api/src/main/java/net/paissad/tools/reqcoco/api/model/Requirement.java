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

@XmlRootElement(name = "requirement")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Requirement implements Serializable {

	private static final long	serialVersionUID	= 1L;

	public static final String	VERSION_UNKNOWN		= "N/A";

	@XmlAttribute(required = true)
	@XmlID
	private String				id;

	@XmlAttribute
	private boolean				ignore;

	@XmlElement(required = true)
	private String				shortDescription;

	@XmlElement
	private String				fullDescription;

	@XmlAttribute(required = true)
	private String				version;

	@XmlAttribute(required = true)
	private String				revision;

	@XmlElement(required = true)
	private boolean				codeDone;

	@XmlElement
	private String				codeAuthor;

	@XmlElement
	private String				codeAuthorComment;

	@XmlElement(required = true)
	private boolean				testDone;

	@XmlElement
	private String				testAuthor;

	@XmlElement
	private String				testAuthorComment;

	@XmlElement
	private String				link;

	public Requirement() {
		// default no-arg constructor
	}

	public Requirement(String id, String version, String revision) {
		this.id = id;
		this.version = version;
		this.revision = revision;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Requirement [id=");
		builder.append(id);
		builder.append(", ignore=");
		builder.append(ignore);
		builder.append(", shortDescription=");
		builder.append(shortDescription);
		builder.append(", version=");
		builder.append(version);
		builder.append(", revision=");
		builder.append(revision);
		builder.append(", codeDone=");
		builder.append(codeDone);
		builder.append(", codeAuthor=");
		builder.append(codeAuthor);
		builder.append(", testDone=");
		builder.append(testDone);
		builder.append(", testAuthor=");
		builder.append(testAuthor);
		builder.append("]");
		return builder.toString();
	}

}
