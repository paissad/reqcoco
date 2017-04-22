package net.paissad.tools.reqcoco.core.report;

import java.net.URI;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.api.exception.ReqParserException;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.core.TestUtil;

public class ReqReportBuilderConsoleImplTest {

	private ReqReportBuilderConsole reqReportBuilderConsole;

	@Before
	public void setUp() throws Exception {
		setUpByUsingUri(TestUtil.REQUIREMENTS_INPUT_FILE1_XML_URI);
	}

	@Test
	public void testBuild() throws ReqReportBuilderException {
		this.reqReportBuilderConsole.build();
	}

	@Test
	public void testBuildEmptyRequirements() throws ReqReportBuilderException, ReqParserException {
		this.setUpByUsingUri(TestUtil.REQUIREMENTS_INPUT_FILE_EMPTY_XML_URI);
		this.reqReportBuilderConsole.build();
	}

	@Test
	public void testGetOutput() {
		Assert.assertEquals(System.out, this.reqReportBuilderConsole.getOutput());
	}

	private void setUpByUsingUri(final URI uri) throws ReqParserException {
		Collection<Requirement> reqs = TestUtil.getRequirementsFromStub(uri, null);
		this.reqReportBuilderConsole = new ReqReportBuilderConsole(reqs);
	}

}
