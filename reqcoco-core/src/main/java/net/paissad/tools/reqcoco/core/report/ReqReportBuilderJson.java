package net.paissad.tools.reqcoco.core.report;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.report.ReqReportConfig;

public class ReqReportBuilderJson extends AbstractReqReportBuilder {

    private static final Logger LOGGER                             = LoggerFactory.getLogger(ReqReportBuilderJson.class);

    /** The default extension for the JSON report builder. */
    public static final String  JSON_REPORT_FILE_DEFAULT_EXTENSION = ".json";

    private static final String LOGGER_PREFIX_TAG                  = String.format("%-15s -", "[JsonReport]");

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PRIVATE)
    private Path                jsonReportFilePath;

    /**
     * The path of the directory where to generated the report
     */
    @Getter(value = AccessLevel.PRIVATE)
    private Path                reportOutputDirPath;

    /** The name of the output file to generate. */
    @Getter(value = AccessLevel.PRIVATE)
    private String              reportFilename;

    /**
     * @param reportOutputDirPath - The directory where to generate the report.
     * @see #ReqReportBuilderJson(Path, String)
     */
    public ReqReportBuilderJson(final Path reportOutputDirPath) {
        this(reportOutputDirPath, null);
    }

    /**
     * @param reportOutputDirPath - The directory where to generate the report.
     * @param reportFilename - The name of the output JSON output file to generate. If <code>null</code> or blank, the default name will be used.
     */
    public ReqReportBuilderJson(final Path reportOutputDirPath, final String reportFilename) {
        this.reportOutputDirPath = reportOutputDirPath;
        this.reportFilename = StringUtils.isBlank(reportFilename) ? getDefaultReportFilename() : reportFilename;
    }

    @Override
    public void configure(final Collection<Requirement> requirements, final ReqReportConfig config) throws ReqReportBuilderException {

        super.configure(requirements, config);

        this.setJsonReportFilePath(Paths.get(getReportOutputDirPath().toString(), getReportFilename()));
        LOGGER.debug("{} JSON report file path --> {}", LOGGER_PREFIX_TAG, getJsonReportFilePath());

        try {
            // We create the output directory before generating the JSON report and we copy the lib/ directory into it
            Files.createDirectories(getReportOutputDirPath());

        } catch (Exception e) {
            throw new ReqReportBuilderException("Error while creating parent directories which are supposed to contain the JSON report file", e);
        }
    }

    @Override
    public void run() throws ReqReportBuilderException {

        try (final Writer out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(getJsonReportFilePath().toFile()), 8192), StandardCharsets.UTF_8)) {

            LOGGER.info("{} Starting to generate JSON report to the directory --> {}", LOGGER_PREFIX_TAG, this.getReportOutputDirPath());

            final Collection<ReqReport> reqReports = buildReqReports();

            final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();

            final GlobalJsonReport globalJsonReport = new GlobalJsonReport();
            globalJsonReport.setReqReportConfig(getReportConfig());
            globalJsonReport.setReports(reqReports);

            gson.toJson(globalJsonReport, out);

            LOGGER.info("{} Finished generating JSON report", LOGGER_PREFIX_TAG);

        } catch (IOException e) {
            String errMsg = "Error while building JSON report : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqReportBuilderException(errMsg, e);
        }
    }

    @Override
    protected String getDefaultFileReportExtension() {
        return JSON_REPORT_FILE_DEFAULT_EXTENSION;
    }

    @Getter
    @Setter
    private static class GlobalJsonReport {

        @Expose
        @SerializedName("reports")
        private Collection<ReqReport> reports;

        @Expose
        @SerializedName("metadata")
        private ReqReportConfig       reqReportConfig;
    }

}
