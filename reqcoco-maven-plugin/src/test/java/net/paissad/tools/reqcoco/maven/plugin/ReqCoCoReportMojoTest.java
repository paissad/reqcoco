package net.paissad.tools.reqcoco.maven.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;
import org.junit.Test;

import net.paissad.tools.reqcoco.runner.ExitStatus;

public class ReqCoCoReportMojoTest extends AbstractMojoTestCase {

    private Path hardcoded_outputdir;;

    protected void setUp() throws Exception {
        super.setUp();
        hardcoded_outputdir = Paths.get(System.getProperty("user.dir"), "target/__hardcoded_test_dir__/custom-output-dir").normalize();
        FileUtils.forceDeleteOnExit(hardcoded_outputdir.toFile());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        FileUtils.deleteQuietly(hardcoded_outputdir.toFile());
    }

    @Test
    public void testExecuteBasicRun() throws Exception {
        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/basic-run-test/pom.xml";
        final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
        Assert.assertNotNull(mojo);
        mojo.execute();
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/html/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.html").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/excel/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.xlsx").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.zip").toFile().exists());
    }

    @Test
    public void testExecuteNoHtmlReport() throws Exception {
        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/no-html-report-test/pom.xml";
        final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
        Assert.assertNotNull(mojo);
        mojo.execute();
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/excel/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.xlsx").toFile().exists());
        Assert.assertFalse(Paths.get(hardcoded_outputdir.toString(), "/html/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.html").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.zip").toFile().exists());
    }

    @Test
    public void testExecuteNoExcelReport() throws Exception {
        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/no-excel-report-test/pom.xml";
        final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
        Assert.assertNotNull(mojo);
        mojo.execute();
        Assert.assertFalse(Paths.get(hardcoded_outputdir.toString(), "/excel/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.xlsx").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/html/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.html").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.zip").toFile().exists());
    }

    @Test
    public void testExecuteNoZipReport() throws Exception {
        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/no-zip-report-test/pom.xml";
        final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
        Assert.assertNotNull(mojo);
        mojo.execute();
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/excel/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.xlsx").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/html/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.html").toFile().exists());
        Assert.assertFalse(Paths.get(hardcoded_outputdir.toString(), "reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.zip").toFile().exists());
    }

    @Test
    public void testExecuteCustomReportName() throws Exception {
        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/custom-report-name-test/pom.xml";
        final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
        Assert.assertNotNull(mojo);
        mojo.execute();
        Assert.assertFalse(Paths.get(hardcoded_outputdir.toString(), "/excel/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.xlsx").toFile().exists());
        Assert.assertFalse(Paths.get(hardcoded_outputdir.toString(), "/html/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.html").toFile().exists());
        Assert.assertFalse(Paths.get(hardcoded_outputdir.toString(), "reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.zip").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/excel/new_report_name.xlsx").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/html/new_report_name.html").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "new_report_name.zip").toFile().exists());
    }

    @Test
    public void testExecuteSkip() throws Exception {
        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/skip-execution-test/pom.xml";
        final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
        Assert.assertNotNull(mojo);
        mojo.execute();
        Assert.assertFalse(Paths.get(hardcoded_outputdir.toString()).toFile().exists());
    }
    
    @Test
    public void testExecuteUnhandledProperties() throws Exception {
        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/unhandled-properties-test/pom.xml";
        final ReqCocoReportMojo mojo = (ReqCocoReportMojo) lookupMojo("report", pluginPom);
        Assert.assertNotNull(mojo);
        mojo.execute();
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/html/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.html").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "/excel/reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage.xlsx").toFile().exists());
        Assert.assertTrue(Paths.get(hardcoded_outputdir.toString(), "reqcoco-maven-plugin-test-9.8.7.6-requirements-coverage" + ".zip").toFile().exists());
    }

    @Test
    public void testExecuteMissingRequiredArg() throws Exception {

        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/missing-required-arg-test/pom.xml";
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
            Assert.assertTrue(MojoExecutionException.class.equals(cause.getClass()));
            Assert.assertTrue(
                    cause.getMessage().startsWith("The ReqRunner program returned an exit code of " + ExitStatus.OPTIONS_PARSING_ERROR.getCode() + ". The arguments are : "));
        }

        Assert.assertNotNull("A 'MojoExecutionException' should have been thrown while executing the mojo", expectedException);
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
            Assert.assertTrue(MojoExecutionException.class.equals(cause.getClass()));
            Assert.assertTrue(cause.getMessage().startsWith("The return code status while generating the reports is : "));
        }

        Assert.assertNotNull("A 'MojoExecutionException' should have been thrown while executing the mojo", expectedException);
    }

    @Test
    public void testExecuteWhileUnableToCreateOutputDir() throws Exception {

        final Path outputDirPath = Paths.get(getBasedir(), "target/__hardcoded_test_dir__/unmodifiable-output-dir");
        if (outputDirPath.toFile().exists()) {
            FileUtils.deleteQuietly(outputDirPath.toFile());
        }

        Files.createFile(outputDirPath); // We create a file rather than a directory in order to generate an I/O error ...

        final String pluginPom = getBasedir() + "/src/test/resources/unit/maventarget/report/unable-create-outputdir-test/pom.xml";
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
            Assert.assertTrue(MojoExecutionException.class.equals(cause.getClass()));
        }

        Assert.assertNotNull("A 'MojoExecutionException' should have been thrown while executing the mojo", expectedException);
    }

}
