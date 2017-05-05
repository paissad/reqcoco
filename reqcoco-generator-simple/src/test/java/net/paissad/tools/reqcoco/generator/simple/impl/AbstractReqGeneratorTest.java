package net.paissad.tools.reqcoco.generator.simple.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.generator.simple.api.ReqGeneratorConfig;
import net.paissad.tools.reqcoco.generator.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorConfigException;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorExecutionException;
import net.paissad.tools.reqcoco.generator.simple.impl.parser.FileReqSourceParser;

public class AbstractReqGeneratorTest {

	private AbstractReqGenerator	reqGenerator;

	private ReqGeneratorConfig		reqGeneratorConfigStub;

	private Path					coverageOutputReportFile;

	@Rule
	public ExpectedException		thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.reqGenerator = new AbstractReqGenerator() {
		};
		this.reqGeneratorConfigStub = this.getConfigStub();
		this.coverageOutputReportFile = Files.createTempFile(getClass().getSimpleName(), "--req-coverage-report.xml");
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(this.coverageOutputReportFile.toFile());
	}

	@Test
	public void testConfigure() throws ReqGeneratorConfigException {

		this.reqGenerator.configure(this.reqGeneratorConfigStub);

		Assert.assertEquals(this.reqGeneratorConfigStub, this.reqGenerator.getConfig());
		Assert.assertTrue(this.reqGeneratorConfigStub == this.reqGenerator.getConfig());
	}

	@Test
	public void testRun() throws ReqGeneratorConfigException, ReqGeneratorExecutionException {

		this.reqGenerator.configure(this.reqGeneratorConfigStub);
		this.reqGenerator.run();
	}

	private ReqGeneratorConfig getConfigStub() {
		return new AbstractReqGeneratorConfig() {

			@Override
			public Path getSourceCodePath() {
				try {
					return Paths.get(AbstractReqGeneratorTest.class.getResource("/samples/input/code/source").toURI());
				} catch (URISyntaxException e) {
					throw new IllegalStateException(e);
				}
			}

			@Override
			public Path getTestsCodePath() {
				try {
					return Paths.get(AbstractReqGeneratorTest.class.getResource("/samples/input/code/test").toURI());
				} catch (URISyntaxException e) {
					throw new IllegalStateException(e);
				}
			}

			@Override
			public URI getSourceRequirements() {
				try {
					return AbstractReqGeneratorTest.class.getResource("/samples/input/req_declarations_1.txt").toURI();
				} catch (URISyntaxException e) {
					throw new IllegalStateException(e);
				}
			}

			@Override
			public ReqSourceParser getSourceParser() {
				return new FileReqSourceParser();
			}

			@Override
			public Path getCoverageOutput() {
				return coverageOutputReportFile;
			}
		};
	}

}
