package net.paissad.tools.reqcoco.parser.simple.api;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.api.model.Status;

/**
 * This class represent the tag which is placed into a code (source or test).
 * 
 * @author paissad
 */
@Getter
@Setter
public class ReqCodeTag {

	/** The id of the requirement which is coded. */
	private String	id;

	/** The version of the requirement which is coded. */
	private String	version;

	/** The revision of the requirement which is coded */
	private String	revision;

	/** The author of the requirement coded. */
	private String	author;

    /** The status of the requirement */
    private Status status;

	/** The optional extra comment added by the author. */
	private String	comment;

}
