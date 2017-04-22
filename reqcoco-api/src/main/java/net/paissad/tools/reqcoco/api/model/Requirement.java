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
@ToString(exclude = { "fullDescription" })
public class Requirement implements Serializable {

	private static final long	serialVersionUID	= 1L;

	@XmlAttribute(required = true)
	@XmlID
	private String				id;

	@XmlElement(required = true)
	private String				name;

	@XmlElement(required = true)
	private String				shortDescription;

	private String				fullDescription;

	@XmlElement
	private Version				version;

	private boolean				codeDone;

	private boolean				testDone;

	public Requirement() {
		// default no-arg constructor
	}

}
