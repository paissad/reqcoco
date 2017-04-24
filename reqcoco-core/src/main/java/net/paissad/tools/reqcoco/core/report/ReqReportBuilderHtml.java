package net.paissad.tools.reqcoco.core.report;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
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
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;

public class ReqReportBuilderHtml extends AbstractReqReportBuilder {

	private static final Logger		LOGGER							= LoggerFactory.getLogger(ReqReportBuilderHtml.class);

	private static final Charset	UTF8							= Charset.forName("UTF8");

	private static final String		TEMPLATES_REPORTS_HTML_LOCATION	= "/templates/reports/html";

	private static final String		DEFAULT_REPORT_FILENAME			= "REPORT-requirements.html";

	/**
	 * The template configuration
	 */
	private Configuration			templateConfig;

	/**
	 * The path of the directory where to create for the HTML report
	 */
	@Getter(value = AccessLevel.PRIVATE)
	private Path					reportOutputDirPath;

	/** The name of the output HTML output file to generate. */
	@Getter(value = AccessLevel.PRIVATE)
	private String					reportFilename;

	/**
	 * @param requirements - The requirements to use for generating the report.
	 * @param reportOutputDirPath - The directory where to generate the report.
	 * @see #ReqReportBuilderHtml(Collection, Path, String)
	 */
	public ReqReportBuilderHtml(final Collection<Requirement> requirements, final Path reportOutputDirPath) {
		this(requirements, reportOutputDirPath, null);
	}

	/**
	 * @param requirements - The requirements to use for generating the report.
	 * @param reportOutputDirPath - The directory where to generate the report.
	 * @param reportFilename - The name of the output HTML output file to generate. If <code>null</code> or blank, the default name will be used.
	 */
	public ReqReportBuilderHtml(final Collection<Requirement> requirements, final Path reportOutputDirPath, final String reportFilename) {
		getRequirements().addAll(requirements);
		this.reportOutputDirPath = reportOutputDirPath;
		this.reportFilename = StringUtils.isBlank(reportFilename) ? DEFAULT_REPORT_FILENAME : reportFilename;
		this.setReportConfig(getDefaultReportConfig());
		initTemplateFormatter();
	}

	private void initTemplateFormatter() {
		LOGGER.debug("Initializing HTML template formatter");
		this.templateConfig = new Configuration(Configuration.VERSION_2_3_26);
		this.templateConfig.setDefaultEncoding("UTF-8");
		this.templateConfig.setLocale(Locale.US);
		this.templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		this.templateConfig.setLogTemplateExceptions(false);
		this.templateConfig.setClassForTemplateLoading(getClass(), TEMPLATES_REPORTS_HTML_LOCATION);
		LOGGER.debug("Finished initialization of HTML template formatter");
	}

	@Override
	protected void build() throws ReqReportBuilderException {

		try (final Writer out = new OutputStreamWriter(getOutput(), UTF8)) {

			LOGGER.info("Starting to generate HTML report");

			final Template template = this.templateConfig.getTemplate("template.html");

			// We create the output directory before generating the HTML report and we copy the lib/ directory into it
			Files.createDirectories(getReportOutputDirPath());
			ZipUtil.unpack(getClass().getResourceAsStream(TEMPLATES_REPORTS_HTML_LOCATION + "/lib.zip"), getReportOutputDirPath().toFile());

			template.process(getDataModel(), out);

			LOGGER.info("Finished generating HTML report");

		} catch (IOException | TemplateException e) {
			String errMsg = "Error while building HTML report : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqReportBuilderException(errMsg, e);
		}

	}

	/**
	 * @return The data model to use for replacing the values in the HTML template.
	 */
	private Map<String, Object> getDataModel() {

		final Map<String, Object> model = new HashMap<>();
		final Map<String, Collection<Requirement>> requirementsMap = new HashMap<>();

		final StringBuilder dataCode = new StringBuilder();
		final StringBuilder dataTests = new StringBuilder();

		// Retrieves available version values
		final Stream<String> versions = getRequirements().stream().map(req -> req.getVersion().getValue()).distinct();

		versions.forEach(version -> {

			// Group requirements by version value
			requirementsMap.put(version, Requirements.getByVersion(getRequirements(), version));

			// Now that all requirements are grouped by version, we can build the data set for the template.
			// We retrieve all requirements for this version
			final Collection<Requirement> reqs = requirementsMap.get(version);

			long codeDoneCount = getCodeDoneCount(version);
			long codeUndoneCount = reqs.size() - codeDoneCount;
			long codeSkippedCount = 0;

			long testsDoneCount = getTestsDoneCount(version);
			long testsUndoneCount = reqs.size() - testsDoneCount;
			long testsSkippedCount = 0;

			final String dataSetEntryFormat = "{Version:'Version %s',freq:{Done:%s, Undone:%s, Skipped:%s}},\n";

			dataCode.append(String.format(dataSetEntryFormat, version, codeDoneCount, codeUndoneCount, codeSkippedCount));
			dataTests.append(String.format(dataSetEntryFormat, version, testsDoneCount, testsUndoneCount, testsSkippedCount));

		});

		model.put("coverage_title", getReportConfig().getTitle());
		model.put("code_coverage_diagram_name", getReportConfig().getCodeCoverageDiagramName());
		model.put("tests_coverage_diagram_name", getReportConfig().getTestsCoverageDiagramName());
		model.put("coverage_table_legend_name", getReportConfig().getRequirementsTableLegend());
		model.put("dataCode", dataCode.toString());
		model.put("dataTests", dataTests.toString());
		model.put("requirements", getRequirements());

		return model;
	}

	@Override
	protected OutputStream getOutput() throws IOException {
		final File htmlReportFile = Paths.get(getReportOutputDirPath().toString(), getReportFilename()).toFile();
		return new BufferedOutputStream(new FileOutputStream(htmlReportFile), 8192);
	}

}
