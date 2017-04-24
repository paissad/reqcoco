package net.paissad.tools.reqcoco.runner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.parser.ReqSourceParser;
import net.paissad.tools.reqcoco.api.report.ReqReportBuilder;
import net.paissad.tools.reqcoco.core.parser.simple.PathReqSourceParser;
import net.paissad.tools.reqcoco.core.report.ReqReportBuilderConsole;
import net.paissad.tools.reqcoco.core.report.ReqReportBuilderHtml;

public class ReqCoCoRunner {

	private static final Logger		LOGGER				= LoggerFactory.getLogger(ReqCoCoRunner.class);

	private static final String		LOGGER_PREFIX_TAG	= String.format("%-15s - ", "[ReqCoCoRunner]");

	@Getter
	private ReqCoCoRunnerOptions	options;

	public ReqCoCoRunner() {
		this.options = new ReqCoCoRunnerOptions();
	}

	public static void main(final String... args) {
		System.exit(new ReqCoCoRunner().proxyMain(args));
	}

	public int proxyMain(final String... args) {

		setLoggingLevel("INFO");

		CmdLineParser parser = null;
		try {
			parser = getRunner().getOptions().parseOptions(args);
		} catch (CmdLineException e1) {
			LOGGER.trace(LOGGER_PREFIX_TAG + "An error occured while parsing the options", e1);
			return getExitCode(ExitStatus.OPTIONS_PARSING_ERROR);
		}

		if (getRunner().getOptions().isHelp()) {
			ReqCoCoRunnerOptions.printUsage(parser);
			return getExitCode(ExitStatus.OK);
		}

		// Sets the log level if specified
		if (getRunner().getOptions().getLogLevel() != null) {
			setLoggingLevel(getRunner().getOptions().getLogLevel());
		}

		try {

			// Parse the input
			final Collection<Requirement> requirements = parseInput(getOptions());

			// Get the report builders
			final Collection<ReqReportBuilder> reportBuilders = getReportBuilders(getOptions());

			// Build the report(s)
			runReportBuilders(reportBuilders, requirements);

		} catch (ReqSourceParserException e) {
			String errMsg = "Error while parsing the source/input : " + e.getMessage();
			LOGGER.error(LOGGER_PREFIX_TAG + errMsg, e);
			return getExitCode(ExitStatus.REQUIREMENTS_INPUT_PARSE_ERROR);

		} catch (ReqReportBuilderException e) {
			String errMsg = "Error while building the report : " + e.getMessage();
			LOGGER.error(LOGGER_PREFIX_TAG + errMsg, e);
			return getExitCode(ExitStatus.BUILD_REPORT_ERROR);
		}

		return getExitCode(ExitStatus.OK);
	}

	private Collection<Requirement> parseInput(final ReqCoCoRunnerOptions options) throws ReqSourceParserException {

		final ReqSourceParser parser = new PathReqSourceParser(getSourcePath(options), null);
		return parser.getRequirements().getRequirements();
	}

	private ReqCoCoRunner getRunner() {
		return this;
	}

	private void runReportBuilders(final Collection<ReqReportBuilder> reportBuilders, final Collection<Requirement> requirements)
	        throws ReqReportBuilderException {

		for (ReqReportBuilder reportBuilder : reportBuilders) {

			LOGGER.info(LOGGER_PREFIX_TAG + "Running report builder");
			reportBuilder.configure(requirements, null);
			reportBuilder.run();
			LOGGER.info(LOGGER_PREFIX_TAG + "Finished running report builder");
		}
	}

	/**
	 * @param options
	 * @return The report builders based on the passed parameters.
	 */
	private Collection<ReqReportBuilder> getReportBuilders(final ReqCoCoRunnerOptions options) {

		final List<ReqReportBuilder> reportBuilders = new LinkedList<>();

		if (options.isBuildConsoleReport()) {
			reportBuilders.add(new ReqReportBuilderConsole());
		}

		if (options.isBuildHtmlReport()) {

			Path htmlReportOutputDirPath = Paths.get(getReportOutputDirPath(options).toString(), "html");

			final ReqReportBuilderHtml htmlReportBuilder = StringUtils.isBlank(options.getReportName())
			        ? new ReqReportBuilderHtml(htmlReportOutputDirPath)
			        : new ReqReportBuilderHtml(htmlReportOutputDirPath, options.getReportName() + ReqReportBuilderHtml.REPORT_FILE_DEFAULT_EXTENSION);

			reportBuilders.add(htmlReportBuilder);
		}

		return reportBuilders;
	}

	/**
	 * @param options
	 * @return The path to the input source containing the requirements to parse.
	 */
	private Path getSourcePath(final ReqCoCoRunnerOptions options) {
		return Paths.get(options.getRequirementSource());
	}

	/**
	 * @param options
	 * @return The path to the output directory where to generate the reports.
	 */
	private Path getReportOutputDirPath(final ReqCoCoRunnerOptions options) {
		return Paths.get(options.getOutputFolder());
	}

	private int getExitCode(final ExitStatus exitStatus) {
		return exitStatus.getCode();
	}

	private static void setLoggingLevel(final String logLevel) {
		final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		final ch.qos.logback.classic.Logger appLogger = loggerContext.getLogger("net.paissad.tools.reqcoco");
		appLogger.setLevel(Level.valueOf(logLevel));
	}
}
