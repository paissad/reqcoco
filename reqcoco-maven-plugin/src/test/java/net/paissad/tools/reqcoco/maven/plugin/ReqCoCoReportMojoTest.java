package net.paissad.tools.reqcoco.maven.plugin;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
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

		final String pluginPom = getBasedir() + "/src/test/resources/unit/basic-run-test/pom.xml";

		final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
		assertNotNull(mojo);
		mojo.execute();

	}

}
