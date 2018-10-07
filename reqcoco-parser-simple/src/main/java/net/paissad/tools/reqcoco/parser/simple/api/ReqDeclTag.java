package net.paissad.tools.reqcoco.parser.simple.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a requirement declaration.
 * 
 * @author paissad
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = { "summary", "link" })
public class ReqDeclTag {

	private String	id;

	private String	version;

	private String	revision;

	private String	summary;
	
    private String  link;
}
