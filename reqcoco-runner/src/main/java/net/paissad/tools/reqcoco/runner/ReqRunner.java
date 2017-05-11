package net.paissad.tools.reqcoco.runner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.report.ReqReportBuilder;
import net.paissad.tools.reqcoco.core.report.ReqReportBuilderConsole;
import net.paissad.tools.reqcoco.core.report.ReqReportBuilderHtml;
import net.paissad.tools.reqcoco.generator.simple.api.ReqGenerator;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorConfigException;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorExecutionException;
import net.paissad.tools.reqcoco.generator.simple.impl.AbstractReqGenerator;
import net.paissad.tools.reqcoco.generator.simple.impl.SimpleReqGeneratorConfig;

public class ReqRunner {

	private static final Logger	LOGGER				= LoggerFactory.getLogger(ReqRunner.class);

	private static final String	LOGGER_PREFIX_TAG	= String.format("%-15s -", "[ReqCoCoRunner]");

	@Getter
	private ReqRunnerOptions	options;

	public ReqRunner() {
		this.options = new ReqRunnerOptions();
	}

	public static void main(final String... args) {

		final ReqRunner reqRunner = new ReqRunner();
		final int parseArgsStatus = reqRunner.parseArguments(args);

		if (ExitStatus.OK.getCode() == parseArgsStatus && !reqRunner.getOptions().isHelp()) {
			System.exit(reqRunner.generateReports());
		} else {
			System.exit(parseArgsStatus);
		}
	}

	public int parseArguments(final String... args) {

		setLoggingLevel("INFO");

		CmdLineParser parser = null;
		try {
			parser = getOptions().parseOptions(args);
			getOptions().parseConfigFile();

		} catch (Exception e) {
			LOGGER.trace(LOGGER_PREFIX_TAG + " An error occured while parsing the options", e);
			return getExitCode(ExitStatus.OPTIONS_PARSING_ERROR);
		}

		if (getOptions().isHelp()) {
			ReqRunnerOptions.printUsage(parser);
		}

		return getExitCode(ExitStatus.OK);
	}

	public int generateReports() {

		// Sets the log level if specified
		if (getRunner().getOptions().getLogLevel() != null) {
			setLoggingLevel(getRunner().getOptions().getLogLevel());
		}

		File temporaryCoverageFile = null;

		try {

			temporaryCoverageFile = Files.createTempFile(getClass().getSimpleName() + "-", "-intermediate-coverage-report.xml").toFile();
			final ReqGenerator reqCoverageGenerator = new AbstractReqGenerator() {
			};

			// Set the config to use for the generator
			final SimpleReqGeneratorConfig coverageGeneratorCfg = new SimpleReqGeneratorConfig();
			coverageGeneratorCfg.setExtraOptions(ReqRunnerOptions.mapFromProperties(getOptions().getConfigProperties()));
			coverageGeneratorCfg.setSourceRequirements(getReqSourceURI());
			coverageGeneratorCfg.setCoverageOutput(temporaryCoverageFile.toPath());
			coverageGeneratorCfg.setSourceCodePath(Paths.get(getOptions().getSourceCodePath()));
			coverageGeneratorCfg.setTestsCodePath(Paths.get(getOptions().getTestCodePath()));
			coverageGeneratorCfg.setSourceParser(getOptions().getSourceType().getParser());

			coverageGeneratorCfg.getFileIncludes().addAll(Arrays.asList(getOptions().getResourceIncludes().split(",")));
			coverageGeneratorCfg.getFileExcludes().addAll(Arrays.asList(getOptions().getResourceExcludes().split(",")));
			coverageGeneratorCfg.getIgnoreList().addAll(Arrays.asList(getOptions().getIgnores().split(",")));

			// Parse the input and build the temporary XML coverage report file
			reqCoverageGenerator.configure(coverageGeneratorCfg);
			final Collection<Requirement> requirements = reqCoverageGenerator.run();

			// Get the human readable report builders
			final Collection<ReqReportBuilder> reportBuilders = getReportBuilders(getOptions());

			// Build the human readable reports
			runReportBuilders(reportBuilders, requirements);

		} catch (URISyntaxException e) {
			String errrMsg = "Error while building the URI from the specified source of declared requiremets : " + e.getMessage();
			LOGGER.error(errrMsg, e);
			return getExitCode(ExitStatus.URI_BUILD_ERROR);

		} catch (IOException e) {
			String errMsg = "Error while creating the temporary/intermediate coverage report : " + e.getMessage();
			LOGGER.error(LOGGER_PREFIX_TAG + " " + errMsg, e);
			return getExitCode(ExitStatus.I_O_ERROR);

		} catch (ReqGeneratorConfigException | ReqGeneratorExecutionException | ReqReportBuilderException e) {
			String errMsg = "Error while building the report : " + e.getMessage();
			LOGGER.error(LOGGER_PREFIX_TAG + " " + errMsg, e);
			return getExitCode(ExitStatus.BUILD_REPORT_ERROR);

		} finally {
			FileUtils.deleteQuietly(temporaryCoverageFile);
		}

		return getExitCode(ExitStatus.OK);
	}

	private URI getReqSourceURI() throws URISyntaxException {
		final String reqSource = getOptions().getRequirementSource();
		if (Pattern.compile("^.*?:/").matcher(reqSource).find()) {
			return new URI(reqSource);
		} else {
			return new URI("file:/" + reqSource.replace("\\", "/"));
		}
	}

	private ReqRunner getRunner() {
		return this;
	}

	private void runReportBuilders(final Collection<ReqReportBuilder> reportBuilders, final Collection<Requirement> requirements)
	        throws ReqReportBuilderException {

		for (ReqReportBuilder reportBuilder : reportBuilders) {

			LOGGER.info("{} Running report builder", LOGGER_PREFIX_TAG);
			reportBuilder.configure(requirements, null);
			reportBuilder.run();
			LOGGER.info("{} Finished running report builder", LOGGER_PREFIX_TAG);
		}
	}

	/**
	 * @param options
	 * @return The report builders based on the passed parameters.
	 */
	private Collection<ReqReportBuilder> getReportBuilders(final ReqRunnerOptions options) {

		final List<ReqReportBuilder> reportBuilders = new LinkedList<>();

		if (getOptions().isReportConsole()) {
			reportBuilders.add(new ReqReportBuilderConsole());
		}

		if (getOptions().isReportHtml()) {

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
	 * @return The path to the output directory where to generate the reports.
	 */
	private Path getReportOutputDirPath(final ReqRunnerOptions options) {
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
