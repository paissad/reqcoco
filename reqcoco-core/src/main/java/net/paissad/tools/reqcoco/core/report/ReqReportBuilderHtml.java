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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.api.report.ReqReportConfig;

public class ReqReportBuilderHtml extends AbstractReqReportBuilder {

	/** The default extension for the HTML report builder. */
	public static final String	HTML_REPORT_FILE_DEFAULT_EXTENSION	= ".html";

	private static final Logger	LOGGER								= LoggerFactory.getLogger(ReqReportBuilderHtml.class);

	private static final String	LOGGER_PREFIX_TAG					= String.format("%-15s -", "[HtmlReport]");

	private static final String	TEMPLATES_REPORTS_HTML_LOCATION		= "/templates/reports/html";

	@Getter(value = AccessLevel.PRIVATE)
	@Setter(value = AccessLevel.PRIVATE)
	private Path				htmlReportFilePath;

	/**
	 * The template configuration
	 */
	private Configuration		templateConfig;

	/**
	 * The path of the directory where to create for the HTML report
	 */
	@Getter(value = AccessLevel.PRIVATE)
	private Path				reportOutputDirPath;

	/** The name of the HTML output file to generate. */
	@Getter(value = AccessLevel.PRIVATE)
	private String				reportFilename;

	/**
	 * @param reportOutputDirPath - The directory where to generate the report.
	 * @see #ReqReportBuilderHtml(Path, String)
	 */
	public ReqReportBuilderHtml(final Path reportOutputDirPath) {
		this(reportOutputDirPath, null);
	}

	/**
	 * @param reportOutputDirPath - The directory where to generate the report.
	 * @param reportFilename - The name of the output HTML output file to generate. If <code>null</code> or blank, the default name will be used.
	 */
	public ReqReportBuilderHtml(final Path reportOutputDirPath, final String reportFilename) {
		this.reportOutputDirPath = reportOutputDirPath;
		this.reportFilename = StringUtils.isBlank(reportFilename) ? getDefaultReportFilename() : reportFilename;
		initTemplateFormatter();
	}

	private void initTemplateFormatter() {
		LOGGER.debug("{} Initializing HTML template formatter", LOGGER_PREFIX_TAG);
		this.templateConfig = new Configuration(Configuration.VERSION_2_3_26);
		this.templateConfig.setDefaultEncoding("UTF-8");
		this.templateConfig.setLocale(Locale.US);
		this.templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		this.templateConfig.setLogTemplateExceptions(false);
		this.templateConfig.setClassForTemplateLoading(getClass(), TEMPLATES_REPORTS_HTML_LOCATION);
		LOGGER.debug("{} Finished initialization of HTML template formatter", LOGGER_PREFIX_TAG);
	}

	@Override
	public void configure(final Collection<Requirement> requirements, final ReqReportConfig config) throws ReqReportBuilderException {

		super.configure(requirements, config);

		this.setHtmlReportFilePath(Paths.get(getReportOutputDirPath().toString(), getReportFilename()));
		LOGGER.debug("{} HTML report file path --> {}", LOGGER_PREFIX_TAG, getHtmlReportFilePath());

		try {
			// We create the output directory before generating the HTML report and we copy the lib/ directory into it
			Files.createDirectories(getReportOutputDirPath());
		} catch (Exception e) {
			throw new ReqReportBuilderException("Error while creating parent directories which are supposed to contain the HTML report file", e);
		}
	}

	@Override
	public void run() throws ReqReportBuilderException {

		try (final Writer out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(getHtmlReportFilePath().toFile()), 8192),
		        StandardCharsets.UTF_8)) {

			LOGGER.info("{} Starting to generate HTML report to the directory --> {}", LOGGER_PREFIX_TAG, this.getReportOutputDirPath());

			final Template template = this.templateConfig.getTemplate("template.html");

			LOGGER.trace("{} Unzipping HTML lib which is to be used for the HTML report file", LOGGER_PREFIX_TAG);
			ZipUtil.unpack(getClass().getResourceAsStream(TEMPLATES_REPORTS_HTML_LOCATION + "/lib.zip"), getReportOutputDirPath().toFile());

			LOGGER.debug("{} Replacing the tokens into the HTML report template ...", LOGGER_PREFIX_TAG);
			template.process(getDataModel(), out);

			LOGGER.info("{} Finished generating HTML report", LOGGER_PREFIX_TAG);

		} catch (IOException | TemplateException e) {
			String errMsg = "Error while building HTML report : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqReportBuilderException(errMsg, e);
		}
	}

	@Override
	protected String getDefaultFileReportExtension() {
		return HTML_REPORT_FILE_DEFAULT_EXTENSION;
	}

	/**
	 * @return The data model to use for replacing the values in the HTML template.
	 */
	private Map<String, Object> getDataModel() {

		LOGGER.trace("{} Building data model to use for the template", LOGGER_PREFIX_TAG);

		final Map<String, Object> model = new HashMap<>();
		final Map<String, Collection<Requirement>> requirementsMap = new HashMap<>();

		final StringBuilder dataCode = new StringBuilder();
		final StringBuilder dataTests = new StringBuilder();

		// Retrieves available versions
		final Stream<String> versions = getRequirements().stream().map(Requirement::getVersion).distinct();

		versions.sorted().forEach(version -> {

			// Group requirements by version
			requirementsMap.put(version, Requirements.getByVersion(getRequirements(), version));

			// This variable holds the number of declared requirements which are ignored for coverage.
			long reqsIgnoredCount = getIgnoredRequirementsCount(version);

			long codeDoneCount = getCodeDoneCount(version);
			long codeUndoneCount = getCodeUndoneCount(version);

			long testsDoneCount = getTestsDoneCount(version);
			long testsUndoneCount = getTestsUndoneCount(version);

			final String dataSetEntryFormat = "{Version:'Version %s',freq:{Done:%s, Undone:%s, Ignored:%s}},\n";

			dataCode.append(String.format(dataSetEntryFormat, version, codeDoneCount, codeUndoneCount, reqsIgnoredCount));
			dataTests.append(String.format(dataSetEntryFormat, version, testsDoneCount, testsUndoneCount, reqsIgnoredCount));

		});

		model.put("coverage_title", getReportConfig().getTitle());
		model.put("code_coverage_diagram_name", getReportConfig().getCodeCoverageDiagramName());
		model.put("tests_coverage_diagram_name", getReportConfig().getTestsCoverageDiagramName());
		model.put("coverage_table_legend_name", getReportConfig().getRequirementsTableLegend());
		model.put("dataCode", dataCode.toString());
		model.put("dataTests", dataTests.toString());
		model.put("requirements", getRequirements());

		LOGGER.trace("{} Finished building data model for the template", LOGGER_PREFIX_TAG);

		return model;
	}

}
