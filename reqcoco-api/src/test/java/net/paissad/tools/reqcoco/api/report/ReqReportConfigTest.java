package net.paissad.tools.reqcoco.api.report;

import java.util.Arrays;
import java.util.Collection;

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
	
	public void testGetFileteredVersions_When_not_set() {
	    Assert.assertNull(cfg.getFilteredVersions());
	}

    public void testGetFileteredVersions_When_set() {
        final Collection<String> filteredVersions = Arrays.asList("v1", "v2");
        cfg.setFilteredVersions(filteredVersions);
        Assert.assertArrayEquals(filteredVersions.toArray(), cfg.getFilteredVersions().toArray());
    }
}
