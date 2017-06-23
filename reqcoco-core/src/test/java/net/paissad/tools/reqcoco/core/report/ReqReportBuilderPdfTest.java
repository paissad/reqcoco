package net.paissad.tools.reqcoco.core.report;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.exception.ReqReportParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.core.TestUtil;

public class ReqReportBuilderPdfTest {

    private ReqReportBuilderPdf reqReportBuilderPdf;

    /** The directory where to save the report */
    private Path                reportOutputPath;

    @Rule
    public ExpectedException    thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.reportOutputPath = Files.createTempDirectory("__reqcoco_pdf_report__");
        FileUtils.forceDeleteOnExit(this.reportOutputPath.toFile());
        this.setUpByUsingUri(TestUtil.REQUIREMENTS_INPUT_FILE2_XML_URI, null);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(this.reportOutputPath.toFile());
    }

    @Test
    public void testRun() throws ReqReportBuilderException {
        this.reqReportBuilderPdf.run();
    }

    @Test
    public void testRunWithCustomReportName() throws ReqReportBuilderException, ReqReportParserException {
        this.setUpByUsingUri(TestUtil.REQUIREMENTS_INPUT_FILE2_XML_URI, "customReportName");
        this.reqReportBuilderPdf.run();
    }

    @Test
    public void testRunWhenOutputExistAndIsAFile() throws ReqReportBuilderException, IOException, ReqReportParserException {

        this.reportOutputPath = Paths.get(this.reportOutputPath.toString(), this.reqReportBuilderPdf.getDefaultReportFilename());
        Files.createFile(this.reportOutputPath);

        thrown.expect(ReqReportBuilderException.class);
        thrown.expectMessage("Error while creating the directory which is supposed to contain the PDF coverage report");
        thrown.expectCause(Is.isA(IOException.class));

        this.setUpByUsingUri(TestUtil.REQUIREMENTS_INPUT_FILE2_XML_URI, null);
    }

    @Test
    public void testRunWhenOutputExistAndIsADirectory() throws ReqReportBuilderException, IOException {

        this.reportOutputPath = Paths.get(this.reportOutputPath.toString(), this.reqReportBuilderPdf.getDefaultReportFilename());
        Files.createDirectory(this.reportOutputPath);

        thrown.expect(ReqReportBuilderException.class);
        thrown.expectMessage("Error while building the PDF coverage report : ");
        thrown.expectCause(Is.isA(IOException.class));

        this.reqReportBuilderPdf.run();
    }

    @Test
    public void testGetOutput() throws IOException {
        Assert.assertNull(this.reqReportBuilderPdf.getOutput());
    }

    private void setUpByUsingUri(final URI uri, final String customReportFilename) throws ReqReportParserException, ReqReportBuilderException {
        final Collection<Requirement> reqs = TestUtil.getRequirementsFromStub(uri, null);
        this.reqReportBuilderPdf = StringUtils.isBlank(customReportFilename) ? new ReqReportBuilderPdf(this.reportOutputPath)
                : new ReqReportBuilderPdf(this.reportOutputPath, customReportFilename);
        this.reqReportBuilderPdf.configure(reqs, null);
    }
}
