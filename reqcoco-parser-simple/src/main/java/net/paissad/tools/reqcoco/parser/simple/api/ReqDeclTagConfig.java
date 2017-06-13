package net.paissad.tools.reqcoco.parser.simple.api;

/**
 * The configuration (regexes mainly) to be used for the tag to be placed in the source where all requirements are declared.
 * 
 * @author paissad
 */
public interface ReqDeclTagConfig {

	/**
	 * <strong>IMPORTANT : </strong> The regex should be defined so that <code>Matcher.group(1)</code> should return the value.
	 * 
	 * @return The regular expression rule to use for extracting a requirement tag definition.
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
	 * @return The regex which returns the value of the 'summary' contained into a tag.
	 */
	String getSummaryRegex();
}
