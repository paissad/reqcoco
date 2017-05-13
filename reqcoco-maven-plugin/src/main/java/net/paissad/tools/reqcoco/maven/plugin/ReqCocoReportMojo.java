package net.paissad.tools.reqcoco.maven.plugin;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.exception.ReqReportParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.parser.ReqReportParser;
import net.paissad.tools.reqcoco.api.report.ReqReportBuilder;
import net.paissad.tools.reqcoco.core.parser.AbstractReqReportParser;
import net.paissad.tools.reqcoco.core.report.ReqReportBuilderHtml;

@Mojo(name = "report", defaultPhase = LifecyclePhase.SITE, requiresProject = true, threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ReqCocoReportMojo extends AbstractReqCoCoMojo {

	/**
	 * The source (file or url) containing the requirements from which to build the report.
	 */
	@Parameter(property = "reqcoco.report.source", required = true, defaultValue = "${basedir}${file.separator}requirements-reqcoco.xml")
	private String			source;

	/**
	 * The output directory where to store the reports.
	 */
	@Parameter(property = "reqcoco.report.outputdir", required = true, defaultValue = "${project.build.directory}${file.separator}reqcoco-reports")
	private File			outputdir;

	/**
	 * Whether or not to generate the HTML report.
	 */
	@Parameter(property = "reqcoco.report.htmlreport", defaultValue = "true", required = false)
	private boolean			htmlreport;

	/**
	 * The report name. If not specified, the default value will be used. The default value is hold by the ReqCoCo Core project, not by this Maven
	 * plugin.
	 */
	@Parameter(property = "reqcoco.report.name", required = false)
	private String			reportname;

	@Parameter(readonly = true, required = false, defaultValue = "${project}")
	private MavenProject	project;

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

		try {

			getLog().info("Build the requirements input parser from source : " + source);
			final ReqReportParser parser = buildRequirementsReportParser();

			getLog().info("Retrieve the requirements by parsing the source ");
			final Collection<Requirement> requirements = parser.getRequirements().getRequirements();

			final Path reportOutputDirPath = outputdir.toPath();
			getLog().info("Output directory for the requirements coverage reports : " + reportOutputDirPath);

			final List<ReqReportBuilder> reportBuilders = new LinkedList<>();

			String reportFilename = null;

			// Initialize the HTML report if specified
			if (htmlreport) {

				getLog().info("Initializing the HTML requirements coverage report builder");
				if (reportname != null) {
					reportFilename = reportname + ReqReportBuilderHtml.HTML_REPORT_FILE_DEFAULT_EXTENSION;
				}

				final ReqReportBuilderHtml htmlReqBuilder = (reportFilename == null) ? new ReqReportBuilderHtml(reportOutputDirPath)
				        : new ReqReportBuilderHtml(reportOutputDirPath, reportFilename);

				reportBuilders.add(htmlReqBuilder);
			}

			getLog().info("Executing all report builders for requirements coverage");

			for (final ReqReportBuilder reqBuilder : reportBuilders) {
				reqBuilder.configure(requirements, null);
				reqBuilder.run();
			}

			getLog().info("Finished generating requirements coverage report");

		} catch (ReqReportParserException e) {
			String errMsg = "Error while parsing the requirements source/input : " + e.getMessage();
			getLog().error(errMsg, e);
			throw new MojoExecutionException(errMsg, e);

		} catch (ReqReportBuilderException e) {
			String errMsg = "Error while generating the requirements coverage reports : " + e.getMessage();
			getLog().error(errMsg, e);
			throw new MojoExecutionException(errMsg, e);
		}

	}

	private ReqReportParser buildRequirementsReportParser() {
		return new AbstractReqReportParser() {

			@Override
			protected URI getURI() throws URISyntaxException {
				// Hack : give a chance and try to normalize the source if the file exists (usually on windows operating systems ...)
				String normalizedSource = source.replace('\\', '/');
				normalizedSource = new File(normalizedSource).exists() ? new File(normalizedSource).toURI().toString() : normalizedSource;
				final URI uri = URI.create(normalizedSource);
				return uri.getScheme() != null ? uri : Paths.get(source).toFile().toURI();
			}

			@Override
			protected Map<String, Object> getOptions() {
				return null;
			}

		};
	}
}
