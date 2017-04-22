package net.paissad.tools.reqcoco.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "requirements")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Requirements implements Serializable {

	private static final long		serialVersionUID	= 1L;

	@XmlElement(name = "requirement")
	private Collection<Requirement>	requirements;

	public Requirements() {
		this.requirements = new LinkedList<>();
	}

}
