package net.paissad.tools.reqcoco.maven.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;
import org.junit.Test;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.exception.ReqReportParserException;
import net.paissad.tools.reqcoco.maven.plugin.util.PathUtils;

public class ReqCoCoReportMojoTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testExecuteBasicRun() throws Exception {
		final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/basic-run-test/pom.xml";
		final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
		Assert.assertNotNull(mojo);
		mojo.execute();
	}

	@Test
	public void testExecuteNonExistentInput() throws Exception {

		final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/non-existent-input/pom.xml";
		final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
		Assert.assertNotNull(mojo);

		Exception expectedException = null;

		try {
			mojo.execute();

		} catch (MojoExecutionException e) {
			expectedException = e;
			Assert.assertNotNull("A MojoExecutionException was expected to be thrown !!!", e);
			Throwable cause = e.getCause();
			Assert.assertNotNull("The cause of the exception should not be null", cause);
			Assert.assertTrue(ReqReportParserException.class.equals(cause.getClass()));
			Assert.assertTrue(cause.getMessage().startsWith("Error while retrieving requirements from the source : "));
		}

		Assert.assertNotNull("A 'ReqSourceParserException' should have been thrown while executing the mojo", expectedException);
	}

	@Test
	public void testExecuteWhileUnableToCreateOutputDir() throws Exception {

		final Path outputDirPath = Paths.get(getBasedir(), "target/__hardcoded_test_dir__/unmodifiable-output-dir");
		if (outputDirPath.toFile().exists()) {
			FileUtils.deleteQuietly(outputDirPath.toFile());
		}

		Files.createDirectories(outputDirPath);

		final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/unable-create-outputdir-test/pom.xml";
		final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
		Assert.assertNotNull(mojo);

		Exception expectedException = null;

		try {

			PathUtils.removeWritePerms(outputDirPath); // Remove the write permissions
			mojo.execute();

		} catch (MojoExecutionException e) {

			expectedException = e;
			Assert.assertNotNull("A MojoExecutionException was expected to be thrown !!!", e);
			Throwable cause = e.getCause();
			Assert.assertNotNull("The cause of the exception should not be null", cause);
			Assert.assertTrue(ReqReportBuilderException.class.equals(cause.getClass()));
		}

		Assert.assertNotNull("A 'ReqReportBuilderException' should have been thrown while executing the mojo", expectedException);
	}

}
