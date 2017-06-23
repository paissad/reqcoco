package net.paissad.tools.reqcoco.core.report;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.AccessLevel;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.report.ReqReportConfig;

public class ReqReportBuilderPdf extends AbstractReqReportBuilder {

    /** The default extension for the PDF report builder. */
    public static final String  PDF_REPORT_FILE_DEFAULT_EXTENSION = ".pdf";

    private static final Logger LOGGER                            = LoggerFactory.getLogger(ReqReportBuilderPdf.class);

    private static final String LOGGER_PREFIX_TAG                 = String.format("%-15s -", "[PdfReport]");

    /** The name of the EXCEL output file to generate. */
    @Getter(value = AccessLevel.PRIVATE)
    private String              reportFilename;

    /**
     * The path of the directory where to create for the HTML report
     */
    @Getter(value = AccessLevel.PRIVATE)
    private Path                reportOutputDirPath;

    /**
     * @param reportOutputDirPath - The directory where to generate the report.
     * @see #ReqReportBuilderPdf(Path, String)
     */
    public ReqReportBuilderPdf(final Path reportOutputDirPath) {
        this(reportOutputDirPath, null);
    }

    /**
     * @param reportOutputDirPath - The directory where to generate the report.
     * @param reportFilename - The name of the output PDF output file to generate. If <code>null</code> or blank, the default name will be used.
     */
    public ReqReportBuilderPdf(final Path reportOutputDirPath, final String reportFilename) {
        this.reportOutputDirPath = reportOutputDirPath;
        this.reportFilename = StringUtils.isBlank(reportFilename) ? getDefaultReportFilename() : reportFilename;
    }

    @Override
    public void configure(final Collection<Requirement> requirements, final ReqReportConfig config) throws ReqReportBuilderException {
        super.configure(requirements, config);
        try {
            Files.createDirectories(this.getReportOutputDirPath());
        } catch (IOException e) {
            throw new ReqReportBuilderException("Error while creating the directory which is supposed to contain the PDF coverage report", e);
        }
    }

    @Override
    public void run() throws ReqReportBuilderException {

        final Path pdfOutputFile = Paths.get(this.getReportOutputDirPath().toString(), this.getReportFilename());

        LOGGER.info("{} Starting to generate PDF report to the file --> {}", LOGGER_PREFIX_TAG, pdfOutputFile);

        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream(pdfOutputFile.toFile()), 8192)) {

            this.setOutput(out);

            // Retrieves available versions
            final List<String> versions = getRequirements().stream().map(Requirement::getVersion).distinct().collect(Collectors.toList());
            Collections.sort(versions);

            LOGGER.trace("{} Creating the .pdf file -> {}", LOGGER_PREFIX_TAG, pdfOutputFile);

            final Document document = new Document();
            PdfWriter.getInstance(document, getOutput());
            final PdfPTable table = new PdfPTable(CellConfig.values().length);

            // Add headers
            for (final CellConfig cellConfig : CellConfig.values()) {
                final PdfPCell pdfCell = new PdfPCell(new Phrase(cellConfig.header));
                table.addCell(pdfCell);
            }

            // Add content
            document.open();
            document.add(new Paragraph("Requirements Coverage"));
            document.add(table);

            document.close();

            LOGGER.info("{} Finished generating PDF report", LOGGER_PREFIX_TAG);

        } catch (Exception e) {
            String errMsg = "Error while building the PDF coverage report : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqReportBuilderException(errMsg, e);
        }
    }

    @Override
    protected String getDefaultFileReportExtension() {
        return PDF_REPORT_FILE_DEFAULT_EXTENSION;
    }

}
