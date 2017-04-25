package net.paissad.tools.reqcoco.generator.simple.api;

import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorConfigException;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorExecutionException;

public interface ReqGenerator {

	/**
	 * @param cfg - The configuration to use
	 * @throws ReqGeneratorExecutionException If an error occurs during the configuration.
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
	 * @throws ReqGeneratorExecutionException If an error occurs during the execution.
	 */
	void run() throws ReqGeneratorExecutionException;
}
