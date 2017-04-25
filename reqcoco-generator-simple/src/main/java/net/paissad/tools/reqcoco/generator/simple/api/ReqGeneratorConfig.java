package net.paissad.tools.reqcoco.generator.simple.api;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;

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
	 * @return The {@link URI} to the place containing the source code to parse.
	 */
	URI getSourceCodePath();

	/**
	 * @return The regular expression rule to use for extracting a requirement tag from the source code.
	 */
	String getRequirementSourceTag();

	/**
	 * @return The {@link URI} to the place containing the tests code to parse.
	 */
	URI getTestsCodePath();

	/**
	 * @return The regular expression rule to use for extracting a requirement tag from the tests code.
	 */
	String getRequirementTestTag();

	/**
	 * @return The path to output file to generate which contains the requirements coverage report.
	 */
	Path getCoverageOutput();

	/**
	 * @return The list of the IDs of the requirements to mark as ignored.
	 */
	Collection<String> getIgnoreList();
}
