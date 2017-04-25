package net.paissad.tools.reqcoco.api.report;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReqReportConfigTest {

	private ReqReportConfig cfg;

	@Before
	public void setUp() {
		cfg = new ReqReportConfig() {
		};
	}

	@Test
	public void testGetTitle() {
		Assert.assertEquals("Requirements Coverage", cfg.getTitle());
	}

	@Test
	public void testGetCodeCoverageDiagramName() {
		Assert.assertEquals("Code coverage", cfg.getCodeCoverageDiagramName());
	}

	@Test
	public void testGetTestsCoverageDiagramName() {
		Assert.assertEquals("Tests coverage", cfg.getTestsCoverageDiagramName());
	}

	@Test
	public void testGetRequirementsTableLegend() {
		Assert.assertEquals("Table of requirements", cfg.getRequirementsTableLegend());
	}

}
