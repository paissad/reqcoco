package net.paissad.tools.reqcoco.runner;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class ReqRunnerOptionsTest {

	private ReqRunnerOptions	options;

	private CmdLineParser			parser;

	@Rule
	public ExpectedException		thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.options = new ReqRunnerOptions();
		options.parseOptions("--config", "/path/to/reqcoco.props", "--input-type", "file", "--input", "/path/to/source", "--output",
		        "/path/to/outputdir", "arg1", "arg2");
		parser = new CmdLineParser(this);
	}

	@Test
	public void testParseOptionsWithHelp() throws CmdLineException {
		options.parseOptions("-h", "aaaaa");
		Assert.assertTrue(options.isHelp());
	}

	@Test
	public void testParseOptionsMissingRequiredSource() throws CmdLineException {
		thrown.expect(CmdLineException.class);
		options.parseOptions("--output", "/path/to/outputdir");
	}

	@Test
	public void testParseOptionsMissingRequiredOutput() throws CmdLineException {
		thrown.expect(CmdLineException.class);
		options.parseOptions("--input", "/path/to/source");
	}

	@Test
	public void testPrintUsage() {
		ReqRunnerOptions.printUsage(parser);
	}

	@Test
	public void testIsHelp() {
		Assert.assertFalse(options.isHelp());
	}

	@Test
	public void testGetSourceType() {
		Assert.assertEquals(ReqSourceType.FILE, options.getSourceType());
	}

	@Test
	public void testGetOutputFolder() {
		Assert.assertEquals("/path/to/outputdir", options.getOutputFolder());
	}

	@Test
	public void testGetRequirementSource() {
		Assert.assertEquals("/path/to/source", options.getRequirementSource());
	}

	@Test
	public void testGetReportName() {
		Assert.assertNull(options.getReportName());
	}

	@Test
	public void testGetArguments() {
		Assert.assertEquals(Arrays.asList(new String[] { "arg1", "arg2" }), options.getArguments());
	}

}
