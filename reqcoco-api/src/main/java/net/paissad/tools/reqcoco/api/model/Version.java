package net.paissad.tools.reqcoco.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "version")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Version implements Serializable {

	private static final long	serialVersionUID	= 1L;

	@XmlAttribute(required = true)
	private String				value;

	@XmlElement
	private Revision			revision;

	public Version() {
		// default no-arg constructor
	}
}
