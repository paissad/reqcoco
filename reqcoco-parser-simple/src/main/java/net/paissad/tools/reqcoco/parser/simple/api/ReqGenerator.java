package net.paissad.tools.reqcoco.parser.simple.api;

import java.util.Collection;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqGeneratorConfigException;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqGeneratorExecutionException;

public interface ReqGenerator {

	/**
	 * @param cfg - The configuration to use
	 * @throws ReqGeneratorConfigException If an error occurs during the configuration.
	 */
	void configure(final ReqGeneratorConfig cfg) throws ReqGeneratorConfigException;

	/**
	 * <p>
	 * Executes the generator in order to create the requirements coverage reports standard XML file.
	 * </p>
	 * <p>
	 * The method {@link #configure(ReqGeneratorConfig)} should be executed before.
	 * </p>
	 * 
	 * @return The requirements after computation between the declared requirements and the requirements into the source/tests code.
	 * @throws ReqGeneratorExecutionException If an error occurs during the execution.
	 */
	Collection<Requirement> run() throws ReqGeneratorExecutionException;
}
