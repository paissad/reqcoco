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

	private Path			sourceCodePath;

	private Path			testsCodePath;

	private Path			reportOutputDirPath;

	@Before
	public void setUp() throws Exception {

		this.runner = new ReqCoCoRunner();
		this.sourceCodePath = Paths.get(getClass().getResource("/input-samples/code/source").toURI());
		this.testsCodePath = Paths.get(getClass().getResource("/input-samples/code/test").toURI());
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
		Assert.assertEquals(ExitStatus.OPTIONS_PARSING_ERROR.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
	}

	@Test
	public void testMainHelpOptionGiven() throws URISyntaxException {
		List<String> args = getSetupArgs(null);
		args.add("-h");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
	}

	@Test
	public void testMainNominalCaseOK() throws URISyntaxException {
		List<String> args = getSetupArgs(null);
		args.add("--log-level");
		args.add("OFF");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
		Assert.assertEquals("OFF", runner.getOptions().getLogLevel());
		Assert.assertTrue("The HTML report directory must exist", Files.exists(Paths.get(this.reportOutputDirPath.toString(), "html")));
	}

	@Test
	public void testMainChangeReportName() throws URISyntaxException {
		List<String> args = getSetupArgs(null);
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
		List<String> args = getSetupArgs(null);
		args.add("--html-report");
		args.add("false");
		args.add("--console-report");
		args.add("true");
		Assert.assertEquals(ExitStatus.OK.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
		Assert.assertTrue("The HTML report directory should not exist", Files.notExists(Paths.get(this.reportOutputDirPath.toString(), "html")));
	}

	@Test
	public void testMainOutputdirIsActuallyAFile() throws URISyntaxException, IOException {

		FileUtils.deleteQuietly(this.reportOutputDirPath.toFile());
		Files.createFile(reportOutputDirPath);
		List<String> args = getSetupArgs(null);
		args.add("--log-level");
		args.add("OFF");
		Assert.assertEquals(ExitStatus.BUILD_REPORT_ERROR.getCode(), runner.proxyMain(args.toArray(new String[args.size()])));
	}

	/**
	 * @return Default options to use for all tests during setUp().
	 * @throws URISyntaxException
	 */
	private List<String> getSetupArgs(final String testResource) throws URISyntaxException {

		String f = StringUtils.isBlank(testResource) ? "req_declarations_1.txt" : testResource;
		String sourceDeclarationPath = Paths.get(getClass().getResource("/input-samples/" + f).toURI()).toString();

		return new ArrayList<>(Arrays.asList(new String[] { "--input", sourceDeclarationPath, "--out", this.reportOutputDirPath.toString(),
		        "--source-code-path", this.sourceCodePath.toString(), "--tests-code-path", this.testsCodePath.toString() }));
	}

}
