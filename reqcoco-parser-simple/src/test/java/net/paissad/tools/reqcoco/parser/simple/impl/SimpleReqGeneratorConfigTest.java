package net.paissad.tools.reqcoco.parser.simple.impl;

import java.net.URI;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.parser.simple.impl.SimpleReqGeneratorConfig;

public class SimpleReqGeneratorConfigTest {

	private SimpleReqGeneratorConfig simpleReqGeneratorConfig;

	@Before
	public void setUp() throws Exception {
		this.simpleReqGeneratorConfig = new SimpleReqGeneratorConfig();
		this.simpleReqGeneratorConfig.setSourceRequirements(URI.create("file:/foo/bar"));
		this.simpleReqGeneratorConfig.setSourceParser(new FileReqSourceParser());
		this.simpleReqGeneratorConfig.setSourceCodePath(Paths.get("dummy_source_code_path"));
		this.simpleReqGeneratorConfig.setTestsCodePath(Paths.get("dummy_tests_code_path"));
		this.simpleReqGeneratorConfig.setCoverageOutput(Paths.get("dummy_output"));
	}

	@Test
	public void testGetSourceRequirements() {
		Assert.assertNotNull(this.simpleReqGeneratorConfig.getSourceRequirements());
	}

	@Test
	public void testGetSourceParser() {
		Assert.assertNotNull(this.simpleReqGeneratorConfig.getSourceParser());
	}

	@Test
	public void testGetSourceCodePath() {
		Assert.assertNotNull(this.simpleReqGeneratorConfig.getSourceCodePath());
	}

	@Test
	public void testGetTestsCodePath() {
		Assert.assertNotNull(this.simpleReqGeneratorConfig.getTestsCodePath());
	}

	@Test
	public void testGetCoverageOutput() {
		Assert.assertNotNull(this.simpleReqGeneratorConfig.getCoverageOutput());
	}

}
