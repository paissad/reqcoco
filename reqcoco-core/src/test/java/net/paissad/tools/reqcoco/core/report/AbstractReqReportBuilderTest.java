package net.paissad.tools.reqcoco.core.report;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
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

		};
	}

	@Test
	public void testGetCodeDoneRatioAnyVersion() {
		Assert.assertEquals(2f / 3f, this.abstractRequirementReportBuilder.getCodeDoneRatio(), 0.0001);
	}

	@Test
	public void testGetCodeDoneRatioV1_0() {
		Assert.assertEquals(1f / 2f, this.abstractRequirementReportBuilder.getCodeDoneRatio("1.0"), 0.0001);
	}

	@Test
	public void testGetTestDoneRatioAnyVersion() {
		Assert.assertEquals(1f / 3f, this.abstractRequirementReportBuilder.getTestDoneRatio(), 0.0001);
	}

	@Test
	public void testGetTestDoneRatioV1_0() {
		Assert.assertEquals(1f / 2f, this.abstractRequirementReportBuilder.getTestDoneRatio("1.0"), 0.0001);
	}

	@Test
	public void testGetRequirements() {
		Assert.assertEquals(this.requirements, this.abstractRequirementReportBuilder.getRequirements());
	}

}
