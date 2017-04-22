package net.paissad.tools.reqcoco.core.report;

import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.core.TestUtil;

public class ReqReportBuilderConsoleImplTest {

	private ReqReportBuilderConsoleImpl reqReportBuilderConsole;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Collection<Requirement> reqs = TestUtil.getRequirementsFromStub(TestUtil.REQUIREMENTS_INPUT_FILE1_XML_URI, null);
		this.reqReportBuilderConsole = new ReqReportBuilderConsoleImpl(reqs);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuild() throws ReqReportBuilderException {
		this.reqReportBuilderConsole.build();
	}

	@Test
	public void testGetOutput() {
		Assert.assertEquals(System.out, this.reqReportBuilderConsole.getOutput());
	}

}
