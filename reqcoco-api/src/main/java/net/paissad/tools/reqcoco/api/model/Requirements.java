package net.paissad.tools.reqcoco.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

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
		this.setRequirements(new LinkedList<>());
	}

	/**
	 * @param requirements - The requirements to filter by version.
	 * @param name - The name value to use for filtering.
	 * @return The collection of requirements having the specified version value.
	 * @exception NullPointerException If either the requirements, or name value passed is <code>null</code>.
	 */
	public static Collection<Requirement> getByName(final Collection<Requirement> requirements, final String name) {
		return requirements.parallelStream().filter(req -> name.equals(req.getName())).collect(Collectors.toList());
	}

	/**
	 * @param requirements - The requirements to filter by version.
	 * @param version - The version value to use for filtering.
	 * @return The collection of requirements having the specified version value.
	 * @exception NullPointerException If either the requirements, or version value passed is <code>null</code>.
	 */
	public static Collection<Requirement> getByVersion(final Collection<Requirement> requirements, final String version) {
		return requirements.parallelStream().filter(req -> version.equals(req.getVersion())).collect(Collectors.toList());
	}

}
