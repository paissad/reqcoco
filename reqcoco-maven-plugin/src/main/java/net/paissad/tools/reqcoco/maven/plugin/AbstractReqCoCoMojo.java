package net.paissad.tools.reqcoco.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;

public abstract class AbstractReqCoCoMojo extends AbstractMojo {

	/**
	 * The default name of the directory containing all the reports generated.
	 */
	public static final String	DEFAULT_OUTPUT_DIRNAME_ROOT	= "reqcoco-reports";

	/**
	 * The default name of the file to use as input/source.
	 */
	public static final String	DEFAULT_SOURCE_FILENAME		= "requirements-reqcoco.xml";
}
