package net.paissad.tools.reqcoco.parser.simple.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the tag which is to placed into the source to parse in order to retrieve the declared requirements.
 * 
 * @author paissad
 */
@Getter
@Setter
@EqualsAndHashCode
public class ReqDeclTag {

	private String	id;

	private String	version;

	private String	revision;

	private String	summary;

}
