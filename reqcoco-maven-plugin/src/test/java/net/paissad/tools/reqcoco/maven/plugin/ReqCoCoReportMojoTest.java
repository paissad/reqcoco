package net.paissad.tools.reqcoco.maven.plugin;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;
import org.junit.Test;

public class ReqCoCoReportMojoTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testExecuteBasicRun() throws Exception {

		// https://maven.apache.org/plugin-developers/plugin-testing.html

		final String pluginPom = getBasedir() + "/src/test/resources/unit/target/report/basic-run-test/pom.xml";

		final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);

		Assert.assertNotNull(mojo);

		mojo.execute();
	}

}
