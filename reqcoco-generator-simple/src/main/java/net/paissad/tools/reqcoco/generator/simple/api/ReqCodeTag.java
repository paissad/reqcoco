package net.paissad.tools.reqcoco.generator.simple.api;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.api.model.Version;

@Getter
@Setter
public class ReqCodeTag {

	/** The id of the requirement which is coded. */
	private String	id;

	/** The version of the requirement which is coded. */
	private Version	version;

	/** The author of the requirement coded. */
	private String	author;

	/** The optional extra comment added by the author. */
	private String	comment;

}
