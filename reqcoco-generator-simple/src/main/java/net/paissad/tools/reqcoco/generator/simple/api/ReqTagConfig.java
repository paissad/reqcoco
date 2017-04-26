package net.paissad.tools.reqcoco.generator.simple.api;

public interface ReqTagConfig {

	/**
	 * @return The regular expression rule to use for extracting a requirement tag from the code.
	 */
	String getCompleteRegex();

	/**
	 * @return The regex which returns the value of the 'id' contained into a tag.
	 */
	String getIdRegex();

	/**
	 * @return The regex which returns the value of the 'version' contained into a tag.
	 */
	String getVersionRegex();

	/**
	 * @return The regex which returns the value of the 'revision' contained into a tag.
	 */
	String getRevisionRegex();

	/**
	 * @return The regex which returns the value of the 'author' contained into a tag.
	 */
	String getAuthorRegex();

	/**
	 * @return The regex which returns the value of the 'comment' contained into a tag.
	 */
	String getCommentRegex();
}
