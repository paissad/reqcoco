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
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import ch.qos.logback.classic.Level;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.report.ReqReportBuilder;
import net.paissad.tools.reqcoco.core.report.AbstractReqReportBuilder;
import net.paissad.tools.reqcoco.core.report.ReqReportBuilderConsole;
import net.paissad.tools.reqcoco.core.report.ReqReportBuilderExcel;
import net.paissad.tools.reqcoco.core.report.ReqReportBuilderHtml;
import net.paissad.tools.reqcoco.parser.simple.api.ReqGenerator;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqGeneratorConfigException;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqGeneratorExecutionException;
import net.paissad.tools.reqcoco.parser.simple.impl.AbstractReqGenerator;
import net.paissad.tools.reqcoco.parser.simple.impl.SimpleReqGeneratorConfig;

public class ReqRunner {

    private static final Logger LOGGER            = LoggerFactory.getLogger(ReqRunner.class);

    private static final String LOGGER_PREFIX_TAG = String.format("%-15s -", "[ReqRunner]");

    @Getter
    private ReqRunnerOptions    options;

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

        setLoggingLevelSafely("INFO");

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
            setLoggingLevelSafely(getRunner().getOptions().getLogLevel());
        }

        try {

            final File rawCoverageFile = Paths.get(getOptions().getOutputFolder(), this.buildRawCoverageFileName()).toFile();
            final ReqGenerator reqCoverageGenerator = new AbstractReqGenerator() {
            };

            // Set the config to use for the generator
            final SimpleReqGeneratorConfig coverageGeneratorCfg = new SimpleReqGeneratorConfig();
            coverageGeneratorCfg.setExtraOptions(ReqRunnerOptions.mapFromProperties(getOptions().getConfigProperties()));
            coverageGeneratorCfg.setSourceRequirements(getReqSourceURI());

            if (getOptions().isCreateRawReportFile()) {
                LOGGER.info("{} The raw report coverage file will be created at '{}'", LOGGER_PREFIX_TAG, rawCoverageFile);
                coverageGeneratorCfg.setCoverageOutput(rawCoverageFile.toPath());
            } else {
                LOGGER.debug("{} Skip the creation of the raw report coverage file.", LOGGER_PREFIX_TAG);
            }

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
            String errrMsg = LOGGER_PREFIX_TAG + " Error while building the URI from the specified source of declared requiremets : " + e.getMessage();
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

        }

        return getExitCode(ExitStatus.OK);
    }

    private String buildRawCoverageFileName() {
        final String reportName = getOptions().getReportName();
        return (StringUtils.isBlank(reportName) ? AbstractReqReportBuilder.DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION : reportName) + ".xml";
    }

    private URI getReqSourceURI() throws URISyntaxException, IOException {
        final String reqSource = getOptions().getRequirementSource();
        if (Pattern.compile("^.*?:/").matcher(reqSource).find()) {
            return new URI(reqSource);
        } else {
            return new URI("file:/" + new File(reqSource).getCanonicalPath().replace("\\", "/").replaceFirst("^/+", ""));
        }
    }

    private ReqRunner getRunner() {
        return this;
    }

    private void runReportBuilders(final Collection<ReqReportBuilder> reportBuilders, final Collection<Requirement> requirements) throws ReqReportBuilderException {

        for (ReqReportBuilder reportBuilder : reportBuilders) {

            LOGGER.info("{} Running report builder", LOGGER_PREFIX_TAG);
            reportBuilder.configure(requirements, null);
            reportBuilder.run();
            LOGGER.info("{} Finished running report builder", LOGGER_PREFIX_TAG);
        }

        if (getOptions().isReportZip() && this.getReportOutputDirPath(getOptions()).toFile().list().length != 0) {

            final String zipFilename = (StringUtils.isBlank(getOptions().getReportName()) ? AbstractReqReportBuilder.DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION
                    : getOptions().getReportName()) + ".zip";
            final Path zipFilePath = Paths.get(this.getReportOutputDirPath(getOptions()).toString(), zipFilename);

            LOGGER.info("{} Archive all reports into a single .zip file : {}", LOGGER_PREFIX_TAG, zipFilePath);

            File temporaryZipFile = null;
            try {
                temporaryZipFile = Files.createTempFile("reqcoco", "zip").toFile();
                FileUtils.deleteQuietly(zipFilePath.toFile()); // Remove the eventually already existing ZIP file before packing the folder.
                ZipUtil.pack(this.getReportOutputDirPath(getOptions()).toFile(), temporaryZipFile);
                Files.move(temporaryZipFile.toPath(), zipFilePath);

            } catch (Exception e) {
                throw new ReqReportBuilderException("Error while creating the report zip file", e);
            } finally {
                FileUtils.deleteQuietly(temporaryZipFile);
            }
        }
    }

    /**
     * @param options
     * @return The report builders based on the passed parameters.
     */
    private Collection<ReqReportBuilder> getReportBuilders(final ReqRunnerOptions options) {

        final List<ReqReportBuilder> reportBuilders = new LinkedList<>();

        if (getOptions().isReportConsole()) {
            LOGGER.debug("{} Including console report", LOGGER_PREFIX_TAG);
            reportBuilders.add(new ReqReportBuilderConsole());
        }

        if (getOptions().isReportHtml()) {

            LOGGER.debug("{} Including HTML report", LOGGER_PREFIX_TAG);
            final Path htmlReportOutputDirPath = Paths.get(getReportOutputDirPath(options).toString(), "html");

            final ReqReportBuilderHtml htmlReportBuilder = StringUtils.isBlank(options.getReportName()) ? new ReqReportBuilderHtml(htmlReportOutputDirPath)
                    : new ReqReportBuilderHtml(htmlReportOutputDirPath, options.getReportName() + ReqReportBuilderHtml.HTML_REPORT_FILE_DEFAULT_EXTENSION);

            reportBuilders.add(htmlReportBuilder);
        }

        if (getOptions().isReportExcel()) {

            LOGGER.debug("{} Including EXCEL report", LOGGER_PREFIX_TAG);
            final Path excelReportOutputDirPath = Paths.get(getReportOutputDirPath(options).toString(), "excel");

            final ReqReportBuilderExcel excelReportBuilder = StringUtils.isBlank(options.getReportName()) ? new ReqReportBuilderExcel(excelReportOutputDirPath)
                    : new ReqReportBuilderExcel(excelReportOutputDirPath, options.getReportName() + ReqReportBuilderExcel.EXCEL_REPORT_FILE_DEFAULT_EXTENSION);

            reportBuilders.add(excelReportBuilder);
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

    private static void setLoggingLevelSafely(final String logLevel) {
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        final Logger appLogger = loggerFactory.getLogger("net.paissad.tools.reqcoco");
        if (appLogger != null && appLogger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) appLogger).setLevel(Level.valueOf(logLevel));
        } else {
            // The log level cannot be changed since it relies on the implementation ...
            // http://stackoverflow.com/questions/2621701/setting-log-level-of-message-at-runtime-in-slf4j?answertab=active#tab-top
        }
    }
}
