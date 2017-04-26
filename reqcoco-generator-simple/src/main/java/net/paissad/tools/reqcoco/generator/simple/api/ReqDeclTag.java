package net.paissad.tools.reqcoco.generator.simple.api;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the tag which is to placed into the source to parse in order to retrieve the declared requirements.
 * 
 * @author paissad
 */
@Getter
@Setter
public class ReqDeclTag {

	private String	id;

	private String	version;

	private String	revision;

	private String	summary;
}
