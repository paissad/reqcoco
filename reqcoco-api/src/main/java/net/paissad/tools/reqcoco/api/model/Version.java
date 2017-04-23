package net.paissad.tools.reqcoco.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "version")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class Version implements Serializable {

	private static final long	serialVersionUID	= 1L;

	/**
	 * Any version, including unknown versions.
	 */
	public static final Version	ANY					= new Version("__any__");

	/**
	 * Unknown versions are <code>null</code>, or blanks.
	 */
	public static final Version	UNKNOWN				= new Version("__unknown__");

	@XmlAttribute(required = true)
	private String				value;

	@XmlElement
	private Revision			revision;

	public Version() {
		// default no-arg constructor
	}

	public Version(final String value) {
		this.value = value;
	}
}
