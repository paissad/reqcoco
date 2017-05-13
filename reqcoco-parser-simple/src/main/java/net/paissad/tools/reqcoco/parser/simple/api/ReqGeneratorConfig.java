package net.paissad.tools.reqcoco.parser.simple.api;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This interface represents the minimum configuration of the generator needed before execution.
 * 
 * @author paissad
 */
public interface ReqGeneratorConfig {

	/**
	 * @return The {@link URI} to the place where all requirements are declared, and for which it is necessary to compute the coverage.
	 */
	URI getSourceRequirements();

	/**
	 * @return The parser to use for retrieving the list of declared requirements.
	 */
	ReqSourceParser getSourceParser();

	/**
	 * @return The configuration which tell how to parse the source where all requirements are declared.
	 */
	ReqDeclTagConfig getDeclTagConfig();

	/**
	 * @return The path (directory or file) containing the source code to parse.
	 */
	Path getSourceCodePath();

	/**
	 * @return The configuration which tells how to parse the <strong>source code</strong> in order to check the requirements.
	 */
	ReqCodeTagConfig getSourceCodeTagConfig();

	/**
	 * @return The path (directory or file) containing the tests code to parse.
	 */
	Path getTestsCodePath();

	/**
	 * @return The configuration which tells how to parse the <strong>tests code</strong> in order to check the requirements.
	 */
	ReqCodeTagConfig getTestsCodeTagConfig();

	/**
	 * @return The list of files to include to the source code and test code parsing. Only files which match this expression or strict names will be
	 *         parsed. Should not be <code>null</code> or empty if an effective report is expected.
	 */
	List<String> getFileIncludes();

	/**
	 * @return The list of files to exclude to the source code and test code parsing. Can be <code>null</code> or empty.
	 */
	List<String> getFileExcludes();

	/**
	 * @return The path to the output file to generate which will contain the requirements coverage XML report.
	 */
	Path getCoverageOutput();

	/**
	 * @return The list of the IDs of the requirements to mark as ignored. Can be empty but not <code>null</code>.
	 */
	Collection<String> getIgnoreList();

	/**
	 * @return The extra options that can be used for many things such as during the parsing of the source of declared requirements. Can be
	 *         <code>null</code>.
	 */
	Map<String, Object> getExtraOptions();
}
