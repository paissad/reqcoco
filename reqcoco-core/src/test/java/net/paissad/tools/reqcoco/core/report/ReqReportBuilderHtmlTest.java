package net.paissad.tools.reqcoco.core.report;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.exception.ReqReportParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.core.TestUtil;

public class ReqReportBuilderHtmlTest {

	private ReqReportBuilderHtml	reqReportBuilderHtml;

	/** The directory where to save the report */
	private Path					reportOutputPath;

	@Before
	public void setUp() throws Exception {
		this.reportOutputPath = Files.createTempDirectory("__reqcoco_html_report__");
		FileUtils.forceDeleteOnExit(this.reportOutputPath.toFile());
		this.setUpByUsingUri(TestUtil.REQUIREMENTS_INPUT_FILE2_XML_URI);
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(this.reportOutputPath.toFile());
	}

	@Test
	public void testRun() throws ReqReportBuilderException {
		this.reqReportBuilderHtml.run();
	}

	@Test
	public void testGetOutput() throws IOException {
		Assert.assertNull(this.reqReportBuilderHtml.getOutput());
	}

	private void setUpByUsingUri(final URI uri) throws ReqReportParserException, ReqReportBuilderException {
		final Collection<Requirement> reqs = TestUtil.getRequirementsFromStub(uri, null);
		this.reqReportBuilderHtml = new ReqReportBuilderHtml(this.reportOutputPath);
		this.reqReportBuilderHtml.configure(reqs, null);
	}

}
