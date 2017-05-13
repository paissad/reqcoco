package net.paissad.tools.reqcoco.core.report;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
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

public class ReqReportBuilderExcelTest {

    private ReqReportBuilderExcel reqReportBuilderExcel;

    /** The directory where to save the report */
    private Path                  reportOutputPath;

    @Rule
    public ExpectedException      thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.reportOutputPath = Files.createTempDirectory("__reqcoco_excel_report__");
        FileUtils.forceDeleteOnExit(this.reportOutputPath.toFile());
        this.setUpByUsingUri(TestUtil.REQUIREMENTS_INPUT_FILE2_XML_URI);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(this.reportOutputPath.toFile());
    }

    @Test
    public void testRun() throws ReqReportBuilderException {
        this.reqReportBuilderExcel.run();
    }

    @Test
    public void testRunWhenOutputExistAndIsADirectory() throws ReqReportBuilderException, IOException {

        this.reportOutputPath = Paths.get(this.reportOutputPath.toString(), this.reqReportBuilderExcel.getDefaultReportFilename());
        Files.createDirectory(this.reportOutputPath);

        thrown.expect(ReqReportBuilderException.class);
        thrown.expectMessage("Error while building the EXCEL coverage report : ");
        thrown.expectCause(Is.isA(IOException.class));

        this.reqReportBuilderExcel.run();
    }

    @Test
    public void testGetOutput() throws IOException {
        Assert.assertNull(this.reqReportBuilderExcel.getOutput());
    }

    private void setUpByUsingUri(final URI uri) throws ReqReportParserException, ReqReportBuilderException {
        final Collection<Requirement> reqs = TestUtil.getRequirementsFromStub(uri, null);
        this.reqReportBuilderExcel = new ReqReportBuilderExcel(this.reportOutputPath);
        this.reqReportBuilderExcel.configure(reqs, null);
    }
}
