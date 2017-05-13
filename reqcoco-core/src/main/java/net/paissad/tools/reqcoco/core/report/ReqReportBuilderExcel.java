package net.paissad.tools.reqcoco.core.report;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.api.report.ReqReportConfig;

public class ReqReportBuilderExcel extends AbstractReqReportBuilder {

    /** The default extension for the EXCEL report builder. */
    public static final String  EXCEL_REPORT_FILE_DEFAULT_EXTENSION = ".xlsx";

    private static final Logger LOGGER                              = LoggerFactory.getLogger(ReqReportBuilderExcel.class);

    private static final String LOGGER_PREFIX_TAG                   = String.format("%-15s -", "[ExcelReport]");

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
     * @see #ReqReportBuilderExcel(Path, String)
     */
    public ReqReportBuilderExcel(final Path reportOutputDirPath) {
        this(reportOutputDirPath, null);
    }

    /**
     * @param reportOutputDirPath - The directory where to generate the report.
     * @param reportFilename - The name of the output EXCEL output file to generate. If <code>null</code> or blank, the default name will be used.
     */
    public ReqReportBuilderExcel(final Path reportOutputDirPath, final String reportFilename) {
        this.reportOutputDirPath = reportOutputDirPath;
        this.reportFilename = StringUtils.isBlank(reportFilename) ? getDefaultReportFilename() : reportFilename;
    }

    @Override
    public void configure(Collection<Requirement> requirements, ReqReportConfig config) throws ReqReportBuilderException {
        super.configure(requirements, config);
        try {
            Files.createDirectories(this.getReportOutputDirPath());
        } catch (IOException e) {
            throw new ReqReportBuilderException("Error while creating the directory which is supposed to contain the EXCEL coverage report", e);
        }
    }

    @Override
    public void run() throws ReqReportBuilderException {

        // https://poi.apache.org/spreadsheet/quick-guide.html

        final Path excelOutputFile = Paths.get(this.getReportOutputDirPath().toString(), this.getReportFilename());

        LOGGER.info("{} Starting to generate EXCEL report to the file --> {}", LOGGER_PREFIX_TAG, excelOutputFile);

        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream(excelOutputFile.toFile()), 8192); final Workbook workbook = new XSSFWorkbook()) {

            // Retrieves available versions
            final Stream<String> versions = getRequirements().stream().map(Requirement::getVersion).distinct();

            versions.sorted().forEach(version -> {

                final Collection<Requirement> reqs = Requirements.getByVersion(getRequirements(), version);

                final Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Version " + version));

                final Header header = sheet.getHeader();
                header.setCenter("Center Header");
                header.setLeft("Left Header");
                header.setRight(HSSFHeader.font("Stencil-Normal", "Italic") + HSSFHeader.fontSize((short) 16) + "Right w/ Stencil-Normal Italic font and size 16");

                this.createHeaders(workbook, sheet);

                int rowIndex = 1;
                for (final Requirement req : reqs) {
                    final Row row = sheet.createRow(rowIndex++);
                    this.populateRow(workbook, row, req);
                }

                final Footer footer = sheet.getFooter();
                footer.setRight("Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages());
            });

            LOGGER.trace("{} Creating the .xlsx file -> {}", LOGGER_PREFIX_TAG, excelOutputFile);
            workbook.write(out);
            LOGGER.info("{} Finished generating EXCEL report", LOGGER_PREFIX_TAG);

        } catch (IOException e) {
            String errMsg = "Error while building the EXCEL coverage report : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqReportBuilderException(errMsg, e);
        }

    }

    private void createHeaders(final Workbook wb, final Sheet sheet) {

        final Row row = sheet.createRow((short) 0);

        final XSSFCellStyle cellStyle = (XSSFCellStyle) wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(Color.GREEN));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        final Font headerFont = wb.createFont();
        headerFont.setBold(true);
        cellStyle.setFont(headerFont);

        final CreationHelper creationHelper = wb.getCreationHelper();

        for (final CELL_CONFIG cellConfig : CELL_CONFIG.values()) {
            final Cell cell = row.createCell(cellConfig.position);
            cell.setCellValue(creationHelper.createRichTextString(cellConfig.header));
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(cellConfig.position);
        }
    }

    private void populateRow(final Workbook wb, final Row row, final Requirement requirement) {

        final CreationHelper creationHelper = wb.getCreationHelper();
        final Sheet currentSheet = row.getSheet();

        for (final CELL_CONFIG cellConfig : CELL_CONFIG.values()) {
            final Cell cell = row.createCell(cellConfig.position);
            final Cell headerCell = currentSheet.getRow(0).getCell(cellConfig.position);
            final CellStyle headerCellStyle = headerCell.getCellStyle();

            final XSSFCellStyle currentCellStyle = (XSSFCellStyle) wb.createCellStyle();
            currentCellStyle.setAlignment(headerCellStyle.getAlignmentEnum());

            cell.setCellStyle(currentCellStyle);
            cell.setCellValue(creationHelper.createRichTextString(getRequirementFieldValue(cellConfig.position, requirement)));

            currentSheet.autoSizeColumn(cellConfig.position);
        }
    }

    private String getRequirementFieldValue(final int position, final Requirement requirement) {
        switch (position) {
        case 0:
            return requirement.getId();
        case 1:
            return requirement.getVersion();
        case 2:
            return requirement.getRevision();
        case 3:
            return requirement.getShortDescription();
        case 4:
            return requirement.isCodeDone() ? "OK" : "KO";
        case 5:
            return requirement.getCodeAuthor();
        case 6:
            return requirement.isTestDone() ? "OK" : "KO";
        case 7:
            return requirement.getTestAuthor();
        case 8:
            return requirement.getLink();
        default:
            throw new IllegalStateException("The position " + position + " is not handled for the Excel report !!!");
        }
    }

    @Override
    protected String getDefaultFileReporttExtension() {
        return EXCEL_REPORT_FILE_DEFAULT_EXTENSION;
    }

    private enum CELL_CONFIG {

                              ID("Id", 0),
                              VERSION("Version", 1),
                              REVISION("Revision", 2),
                              DESCRIPTION("Short Description", 3),
                              CODE("Code", 4),
                              CODE_AUTHOR("Code Author", 5),
                              TEST("Test", 6),
                              TEST_AUTHOR("Test Author", 7),
                              LINK("Link", 8);

        private final String header;
        private final int    position;

        private CELL_CONFIG(String columnHeaderName, int columnPosition) {
            this.header = columnHeaderName;
            this.position = columnPosition;
        }
    }

}
