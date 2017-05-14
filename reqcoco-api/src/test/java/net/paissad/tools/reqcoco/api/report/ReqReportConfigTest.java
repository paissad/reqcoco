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
		Assert.assertEquals("Source Code Coverage", cfg.getCodeCoverageDiagramName());
	}

	@Test
	public void testGetTestsCoverageDiagramName() {
		Assert.assertEquals("Tests Code Coverage", cfg.getTestsCoverageDiagramName());
	}

	@Test
	public void testGetRequirementsTableLegend() {
		Assert.assertEquals("Table of requirements", cfg.getRequirementsTableLegend());
	}

}
