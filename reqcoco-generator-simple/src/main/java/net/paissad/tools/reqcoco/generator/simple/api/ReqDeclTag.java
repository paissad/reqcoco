package net.paissad.tools.reqcoco.generator.simple.api;

import lombok.Setter;
import net.paissad.tools.reqcoco.api.model.Revision;
import net.paissad.tools.reqcoco.api.model.Version;
import lombok.Getter;

/**
 * This class represents the tag which is to placed into the source to parse in order to retrieve the declared requirements.
 * 
 * @author paissad
 */
@Getter
@Setter
public class ReqDeclTag {

	private String		id;

	private Version		version;

	private Revision	revision;

	private String		summary;
}
