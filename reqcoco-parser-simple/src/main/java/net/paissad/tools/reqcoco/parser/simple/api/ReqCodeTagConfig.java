package net.paissad.tools.reqcoco.parser.simple.api;

/**
 * The configuration (regexes mainly) to be used for the tag to be placed into the code (source code or test code).
 * 
 * @author paissad
 */
public interface ReqCodeTagConfig {

	/**
	 * <strong>IMPORTANT : </strong> The regex should be defined so that <code>Matcher.group(1)</code> should return the value.
	 * 
	 * @return The regular expression rule to use for extracting a requirement tag from the code.
	 */
	String getCompleteRegex();

	/**
	 * <strong>IMPORTANT : </strong> The regex should be defined so that <code>Matcher.group(1)</code> should return the value.
	 * 
	 * @return The regex which returns the value of the 'id' contained into a tag.
	 */
	String getIdRegex();

	/**
	 * <strong>IMPORTANT : </strong> The regex should be defined so that <code>Matcher.group(1)</code> should return the value.
	 * 
	 * @return The regex which returns the value of the 'version' contained into a tag.
	 */
	String getVersionRegex();

	/**
	 * <strong>IMPORTANT : </strong> The regex should be defined so that <code>Matcher.group(1)</code> should return the value.
	 * 
	 * @return The regex which returns the value of the 'revision' contained into a tag.
	 */
	String getRevisionRegex();

	/**
	 * <strong>IMPORTANT : </strong> The regex should be defined so that <code>Matcher.group(1)</code> should return the value.
	 * 
	 * @return The regex which returns the value of the 'author' contained into a tag.
	 */
	String getAuthorRegex();

	/**
	 * <strong>IMPORTANT : </strong> The regex should be defined so that <code>Matcher.group(1)</code> should return the value.
	 * 
	 * @return The regex which returns the value of the 'comment' contained into a tag.
	 */
	String getCommentRegex();
}
