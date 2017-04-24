package net.paissad.tools.reqcoco.maven.plugin;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "report", defaultPhase = LifecyclePhase.SITE)
public class ReqCocoReportMojo extends AbstractReqCoCoMojo {

	/**
	 * The source (file or url) containing the requirements from which to build the report.
	 */
	@Parameter(property = "reqcoco.report.source", required = true, defaultValue = "${basedir}${file.separator}requirements-reqcoco.xml")
	private String	source;

	/**
	 * The output directory where to store the reports.
	 */
	@Parameter(property = "reqcoco.report.outputdir", required = true, defaultValue = "${project.build.directory}${file.separator}reqcoco-reports")
	private File	outputdir;

	/**
	 * Whether or not to generate the HTML report.
	 */
	@Parameter(property = "reqcoco.report.htmlreport", defaultValue = "true", required = false)
	private boolean	htmlreport;

	/**
	 * The report name. If not specified, the default value will be used. The default value is hold by the ReqCoCo Core project, not by this Maven
	 * plugin.
	 */
	@Parameter(property = "reqcoco.report.name", required = false)
	private String	reportname;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException {

		String format = "############# %-15s : %s";
		getLog().debug("======================= ReqCoCo parameters =====================================");
		getLog().debug(String.format(format, "source", source));
		getLog().debug(String.format(format, "outputdir", outputdir));
		getLog().debug(String.format(format, "htmlreport", htmlreport));
		getLog().debug(String.format(format, "reportname", reportname));
		getLog().debug("================================================================================");

	}
}
