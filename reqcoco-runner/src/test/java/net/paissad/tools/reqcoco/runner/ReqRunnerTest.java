package net.paissad.tools.reqcoco.runner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.core.report.AbstractReqReportBuilder;

public class ReqRunnerTest {

	private ReqRunner	runner;

	private Path		configFilePath;

	private Path		reportOutputDirPath;

	@Before
	public void setUp() throws Exception {

		this.runner = new ReqRunner();
		this.configFilePath = Paths.get(getClass().getResource("/config/reqcoco.properties").toURI());
		this.reportOutputDirPath = Files.createTempDirectory("_reqcocorunner_output_");
		FileUtils.forceDeleteOnExit(this.reportOutputDirPath.toFile());
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(this.reportOutputDirPath.toFile());
	}

	@Test
	public void testMainWrongOptionGiven() throws URISyntaxException {
		List<String> args = getSetupArgs(null);
		args.add("--very-bad-option");
		Assert.assertEquals(ExitStatus.OPTIONS_PARSING_ERROR.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
	}

	@Test
	public void testMainHelpOptionGiven() throws URISyntaxException {
		List<String> args = getSetupArgs(null);
		args.add("-h");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
	}

	@Test
	public void testMainNominalCaseOK() throws URISyntaxException {
		List<String> args = getSetupArgs(null);
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.generateReports());
		Assert.assertEquals("TRACE", runner.getOptions().getLogLevel());
		Assert.assertTrue("The HTML report directory must exist", Files.exists(Paths.get(this.reportOutputDirPath.toString(), "html")));
        Assert.assertTrue("The ZIP file must exit by default",
                Files.exists(Paths.get(this.reportOutputDirPath.toString(), AbstractReqReportBuilder.DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION + ".zip")));
        Assert.assertFalse("The raw report coverage file must not be created by default",
                Files.exists(Paths.get(this.reportOutputDirPath.toString(), AbstractReqReportBuilder.DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION + ".xml")));
	}

	@Test
	public void testMainChangeReportName() throws URISyntaxException {
		List<String> args = getSetupArgs(null);
		args.add("--report-name");
		args.add("custom_project_name");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.generateReports());
		Assert.assertTrue("The HTML report name is wrong",
		        Files.exists(Paths.get(this.reportOutputDirPath.toString(), "html/custom_project_name.html")));
	}

    @Test
    public void testMainCreateRawCoverageFileAsWell() throws URISyntaxException {
        List<String> args = getSetupArgs(null);
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
        runner.getOptions().setCreateRawReportFile(true); // /!\ change the value after the arguments are parsed !!!
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.generateReports());
        Assert.assertEquals("TRACE", runner.getOptions().getLogLevel());
        Assert.assertTrue("The HTML report directory must exist", Files.exists(Paths.get(this.reportOutputDirPath.toString(), "html")));
        Assert.assertTrue("The raw report coverage file must exist",
                Files.exists(Paths.get(this.reportOutputDirPath.toString(), AbstractReqReportBuilder.DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION + ".xml")));
    }

    @Test
	public void testMainNoHtmlButConsoleReport() throws URISyntaxException {
		List<String> args = getSetupArgs(null);
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
		runner.getOptions().setReportHtml(false);
		runner.getOptions().setReportConsole(true);
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.generateReports());
		Assert.assertTrue("The HTML report directory should not exist", Files.notExists(Paths.get(this.reportOutputDirPath.toString(), "html")));
	}

    @Test
    public void testMainNoExcelReport() throws URISyntaxException {
        List<String> args = getSetupArgs(null);
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
        runner.getOptions().setReportExcel(false);
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.generateReports());
        Assert.assertTrue("The EXCEL report directory should not exist", Files.notExists(Paths.get(this.reportOutputDirPath.toString(), "excel")));
    }

    @Test
    public void testMainNoZipReport() throws URISyntaxException {
        List<String> args = getSetupArgs(null);
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
        runner.getOptions().setReportZip(false);
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.generateReports());
        Assert.assertTrue("The ZIP report file should not exist",
                Files.notExists(Paths.get(this.reportOutputDirPath.toString(), AbstractReqReportBuilder.DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION + ".zip")));
    }

    @Test
    public void testMainNoExcelAndNoHtmlReports() throws URISyntaxException {
        List<String> args = getSetupArgs(null);
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
        runner.getOptions().setReportHtml(false);
        runner.getOptions().setReportExcel(false);
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.generateReports());
        Assert.assertTrue("The HTML report directory should not exist", Files.notExists(Paths.get(this.reportOutputDirPath.toString(), "html")));
        Assert.assertTrue("The EXCEL report directory should not exist", Files.notExists(Paths.get(this.reportOutputDirPath.toString(), "excel")));
    }

    @Test
    public void testMainSourceIsABadURI() throws URISyntaxException, IOException {

        List<String> args = getSetupArgs("file:////this_is_a_bad_source_as_it_contains_too_many_forward_slashes", false);
        Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
        runner.getOptions().setLogLevel("OFF");
        Assert.assertEquals(ExitStatus.BUILD_REPORT_ERROR.getCode(), runner.generateReports());
    }

	@Test
	public void testMainOutputdirIsActuallyAFile() throws URISyntaxException, IOException {

		FileUtils.deleteQuietly(this.reportOutputDirPath.toFile());
		Files.createFile(reportOutputDirPath);
		List<String> args = getSetupArgs(null);
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.parseArguments(args.toArray(new String[args.size()])));
		runner.getOptions().setLogLevel("OFF");
		Assert.assertEquals(ExitStatus.BUILD_REPORT_ERROR.getCode(), runner.generateReports());
	}

    /**
     * @return Default options to use for all tests during setUp().
     * @throws URISyntaxException
     */
    private List<String> getSetupArgs(final String testResource) throws URISyntaxException {
        return getSetupArgs(testResource, true);
    }

    /**
	 * @return Default options to use for all tests during setUp().
	 * @throws URISyntaxException
	 */
	private List<String> getSetupArgs(final String testResource, boolean locatedIntoSampleDir) throws URISyntaxException {

		String f = StringUtils.isBlank(testResource) ? "req_declarations_1.txt" : testResource;
		String sourceDeclarationPath = locatedIntoSampleDir ? Paths.get(getClass().getResource("/input-samples/" + f).toURI()).toString() : f;

		return new ArrayList<>(Arrays.asList(new String[] { "--input-type", "FILE", "--input", sourceDeclarationPath, "--output",
		        this.reportOutputDirPath.toString(), "--config", configFilePath.toString() }));
	}

}
