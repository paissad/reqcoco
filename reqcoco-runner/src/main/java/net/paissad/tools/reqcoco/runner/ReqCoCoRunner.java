package net.paissad.tools.reqcoco.runner;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import lombok.Getter;

public class ReqCoCoRunner {

	private static final Logger		LOGGER	= LoggerFactory.getLogger(ReqCoCoRunner.class);

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
			LOGGER.trace("An error occured while parsing the options", e1);
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

		return getExitCode(ExitStatus.OK);
	}

	private ReqCoCoRunner getRunner() {
		return this;
	}

	private int getExitCode(final ExitStatus exitStatus) {
		return exitStatus.getCode();
	}

	private static void setLoggingLevel(final String logLevel) {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
		        .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Level level = Level.valueOf(logLevel);
		root.setLevel(level);
	}
}
