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

public class ReqCoCoRunnerTest {

	private ReqCoCoRunner	runner;

	private Path			reportOutputDirPath;

	@Before
	public void setUp() throws Exception {

		this.runner = new ReqCoCoRunner();
		this.reportOutputDirPath = Files.createTempDirectory("_reqcocorunner_output_");
		FileUtils.forceDeleteOnExit(this.reportOutputDirPath.toFile());
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(this.reportOutputDirPath.toFile());
	}

	@Test
	public void testMainWrongOptionGiven() {
		List<String> args = getSetupArgs();
		args.add("--very-bad-option");
		Assert.assertEquals(ExitStatus.OPTIONS_PARSING_ERROR.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
	}

	@Test
	public void testMainHelpOptionGiven() {
		List<String> args = getSetupArgs();
		args.add("-h");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
	}

	@Test
	public void testMainNominalCaseOK() throws URISyntaxException {
		List<String> args = getArgs(null);
		args.add("--log-level");
		args.add("OFF");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
		Assert.assertEquals("OFF", runner.getOptions().getLogLevel());
		Assert.assertTrue("The HTML report directory must exist", Files.exists(Paths.get(this.reportOutputDirPath.toString(), "html")));
	}

	@Test
	public void testMainChangeReportName() throws URISyntaxException {
		List<String> args = getArgs(null);
		args.add("--log-level");
		args.add("OFF");
		args.add("--report-name");
		args.add("custom_project_name");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
		Assert.assertEquals("OFF", runner.getOptions().getLogLevel());
		Assert.assertTrue("The HTML report name is wrong",
		        Files.exists(Paths.get(this.reportOutputDirPath.toString(), "html/custom_project_name.html")));
	}

	@Test
	public void testMainNoHtmlButConsoleReport() throws URISyntaxException {
		List<String> args = getArgs(null);
		args.add("--html-report");
		args.add("false");
		args.add("--console-report");
		args.add("true");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
		Assert.assertTrue("The HTML report directory should not exist", Files.notExists(Paths.get(this.reportOutputDirPath.toString(), "html")));
	}

	@Test
	public void testMainInputSourceIsBad() throws URISyntaxException {
		List<String> args = getArgs("file_with_bad_content.xml");
		args.add("--log-level");
		args.add("OFF");
		Assert.assertEquals(ExitStatus.REQUIREMENTS_INPUT_PARSE_ERROR.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
	}

	@Test
	public void testMainOutputdirIsActuallyAFile() throws URISyntaxException, IOException {

		FileUtils.deleteQuietly(this.reportOutputDirPath.toFile());
		Files.createFile(reportOutputDirPath);
		List<String> args = getArgs(null);
		args.add("--log-level");
		args.add("OFF");
		Assert.assertEquals(ExitStatus.BUILD_REPORT_ERROR.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
	}

	private List<String> getArgs(final String testResource) throws URISyntaxException {

		String f = StringUtils.isBlank(testResource) ? "file2.xml" : testResource;
		String sourcePath = Paths.get(getClass().getResource("/input-samples/" + f).toURI()).toString();

		return new ArrayList<>(Arrays.asList(new String[] { "--in", sourcePath, "--out", this.reportOutputDirPath.toString() }));
	}

	/**
	 * @return Default options to use for all tests during setUp().
	 */
	private List<String> getSetupArgs() {
		return new ArrayList<>(Arrays.asList(new String[] { "--in", "/path/to/source", "--out", this.reportOutputDirPath.toString() }));
	}

}
