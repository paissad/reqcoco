package net.paissad.tools.reqcoco.core.report;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.report.ReqReportConfig;
import net.paissad.tools.reqcoco.core.TestUtil;

public class AbstractReqReportBuilderTest {

	private Collection<Requirement>		requirements;

	private AbstractReqReportBuilder	abstractRequirementReportBuilder;

	@Before
	public void setUp() throws Exception {
		this.requirements = TestUtil.getRequirementsFromStub(TestUtil.REQUIREMENTS_INPUT_FILE1_XML_URI, null);
		this.abstractRequirementReportBuilder = new AbstractReqReportBuilder() {

			@Override
			protected Collection<Requirement> getRequirements() {
				return requirements;
			}

			@Override
			public void run() throws ReqReportBuilderException {
				// Do nothing
			}

			@Override
			protected String getDefaultFileReportExtension() {
				return null;
			}

		};
	}

	@Test
	public void testConfigure() throws ReqReportBuilderException {
		this.abstractRequirementReportBuilder.configure(requirements, null);
		Assert.assertNotNull(this.abstractRequirementReportBuilder.getReportConfig());
		Assert.assertTrue(this.abstractRequirementReportBuilder.getDefaultReportConfig() == this.abstractRequirementReportBuilder.getReportConfig());
	}

	@Test
	public void testConfigureWithNonNullConfig() throws ReqReportBuilderException {
		this.abstractRequirementReportBuilder.configure(requirements, new ReqReportConfig() {
		});
		Assert.assertFalse(this.abstractRequirementReportBuilder.getDefaultReportConfig() == this.abstractRequirementReportBuilder.getReportConfig());
	}

	@Test
	public void testGetCodeDoneCount() {
		Assert.assertEquals(2, this.abstractRequirementReportBuilder.getCodeDoneCount());
	}

	@Test
	public void testGetCodeDoneCountV1_0() {
		Assert.assertEquals(1, this.abstractRequirementReportBuilder.getCodeDoneCount("1.0"));
	}

	@Test
	public void testGetCodeUndoneCount() {
		Assert.assertEquals(1, this.abstractRequirementReportBuilder.getCodeUndoneCount());
	}

	@Test
	public void testGetCodeUndoneCountV1_0() {
		Assert.assertEquals(1, this.abstractRequirementReportBuilder.getCodeUndoneCount("1.0"));
	}

	@Test
	public void testGetCodeUndoneCountV1_1() {
		Assert.assertEquals(0, this.abstractRequirementReportBuilder.getCodeUndoneCount("1.1"));
	}

	@Test
	public void testGetTestsDoneCount() {
		Assert.assertEquals(1, this.abstractRequirementReportBuilder.getTestsDoneCount());
	}

	@Test
	public void testGetTestsUndoneCount() {
		Assert.assertEquals(2, this.abstractRequirementReportBuilder.getTestsUndoneCount());
	}

	@Test
	public void testGetTestsDoneCountV1_1() {
		Assert.assertEquals(0, this.abstractRequirementReportBuilder.getTestsDoneCount("1.1"));
	}

	@Test
	public void testGetTestsUndoneCountV1_0() {
		Assert.assertEquals(1, this.abstractRequirementReportBuilder.getTestsUndoneCount("1.0"));
	}

	@Test
	public void testGetCodeDoneRatioAnyVersion() {
		Assert.assertEquals(2f / 5f, this.abstractRequirementReportBuilder.getCodeDoneRatio(), 0.0001);
	}

	@Test
	public void testGetCodeDoneRatioV1_0() {
		Assert.assertEquals(1f / 4f, this.abstractRequirementReportBuilder.getCodeDoneRatio("1.0"), 0.0001);
	}

	@Test
	public void testGetTestDoneRatioAnyVersion() {
		Assert.assertEquals(1f / 5f, this.abstractRequirementReportBuilder.getTestDoneRatio(), 0.0001);
	}

	@Test
	public void testGetTestDoneRatioV1_0() {
		Assert.assertEquals(1f / 4f, this.abstractRequirementReportBuilder.getTestDoneRatio("1.0"), 0.0001);
	}

	@Test
	public void testGetIgnoredRequirementsCount() {
		Assert.assertEquals(2, this.abstractRequirementReportBuilder.getIgnoredRequirementsCount());
	}

	@Test
	public void testGetIgnoredRequirementsCountByVersion() {
		Assert.assertEquals(2, this.abstractRequirementReportBuilder.getIgnoredRequirementsCount("1.0"));
		Assert.assertEquals(0, this.abstractRequirementReportBuilder.getIgnoredRequirementsCount("1.1"));
	}

	@Test
	public void testGetRequirements() {
		Assert.assertEquals(this.requirements, this.abstractRequirementReportBuilder.getRequirements());
		Assert.assertEquals(5, this.abstractRequirementReportBuilder.getRequirements().size());
	}

	@Test
	public void testGetDefaultReportConfig() {
		Assert.assertNotNull(this.abstractRequirementReportBuilder.getDefaultReportConfig());
		this.abstractRequirementReportBuilder.setReportConfig(null);
		Assert.assertNotNull(this.abstractRequirementReportBuilder.getDefaultReportConfig());
	}

	@Test
	public void testGetDefaultReportFilename() {
		Assert.assertEquals(
		        AbstractReqReportBuilder.DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION
		                + abstractRequirementReportBuilder.getDefaultFileReportExtension(),
		        this.abstractRequirementReportBuilder.getDefaultReportFilename());
	}
}
