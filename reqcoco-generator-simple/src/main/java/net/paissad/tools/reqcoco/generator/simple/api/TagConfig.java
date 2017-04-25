package net.paissad.tools.reqcoco.generator.simple.api;

import net.paissad.tools.reqcoco.api.model.Version;

public interface TagConfig {

	/**
	 * @return The id of the requirement which is coded.
	 */
	String getReqId();

	/**
	 * @return The version of the requirement which is coded.
	 */
	Version getVersion();

	/**
	 * @return The author of the requirement coded.
	 */
	String getAuthor();

	/**
	 * @return The optional extra comment added by the author.
	 */
	String getComment();

	/**
	 * @return The regular expression rule to use for extracting a requirement tag from the code.
	 */
	String getRegexp();
}
