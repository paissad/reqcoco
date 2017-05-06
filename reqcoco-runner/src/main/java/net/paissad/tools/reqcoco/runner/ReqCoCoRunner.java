package net.paissad.tools.reqcoco.runner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
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
import net.paissad.tools.reqcoco.generator.simple.impl.parser.FileReqSourceParser;

public class ReqCoCoRunner {

	private static final Logger		LOGGER				= LoggerFactory.getLogger(ReqCoCoRunner.class);

	private static final String		LOGGER_PREFIX_TAG	= String.format("%-15s -", "[ReqCoCoRunner]");

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
			LOGGER.trace(LOGGER_PREFIX_TAG + " An error occured while parsing the options", e1);
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

		File temporaryCoverageFile = null;

		try {

			temporaryCoverageFile = Files.createTempFile(getClass().getSimpleName() + "-", "-intermediate-coverage-report.xml").toFile();
			final ReqGenerator reqCoverageGenerator = new AbstractReqGenerator() {
			};

			// Set the config to use for the generator
			final SimpleReqGeneratorConfig coverageGeneratorCfg = new SimpleReqGeneratorConfig();
			coverageGeneratorCfg.setSourceRequirements(Paths.get(getOptions().getRequirementSource()).toUri());
			coverageGeneratorCfg.setCoverageOutput(temporaryCoverageFile.toPath());
			coverageGeneratorCfg.setSourceCodePath(Paths.get(getOptions().getSourceCodePath()));
			coverageGeneratorCfg.setTestsCodePath(Paths.get(getOptions().getTestsCodePath()));
			coverageGeneratorCfg.setSourceParser(new FileReqSourceParser());

			// TODO: Add the possibility to set includes, excludes & ignoreList without hardcoding !!!

			coverageGeneratorCfg.getFileIncludes().add("*.txt");
			coverageGeneratorCfg.getFileExcludes().add("*.bin");
			coverageGeneratorCfg.getIgnoreList().add("req_2");

			// Parse the input and build the temporary XML coverage report file
			reqCoverageGenerator.configure(coverageGeneratorCfg);
			final Collection<Requirement> requirements = reqCoverageGenerator.run();

			// Get the human readable report builders
			final Collection<ReqReportBuilder> reportBuilders = getReportBuilders(getOptions());

			// Build the human readable reports
			runReportBuilders(reportBuilders, requirements);

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

	private ReqCoCoRunner getRunner() {
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
	private Collection<ReqReportBuilder> getReportBuilders(final ReqCoCoRunnerOptions options) {

		final List<ReqReportBuilder> reportBuilders = new LinkedList<>();

		if (Boolean.valueOf(options.getBuildConsoleReport())) {
			reportBuilders.add(new ReqReportBuilderConsole());
		}

		if (Boolean.valueOf(options.getBuildHtmlReport())) {

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
