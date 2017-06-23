package net.paissad.tools.reqcoco.core.report;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
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

    private static final int    DEFAULT_ZOOM_SCALE                  = 100;

    /** The default extension for the EXCEL report builder. */
    public static final String  EXCEL_REPORT_FILE_DEFAULT_EXTENSION = ".xlsx";

    private static final Logger LOGGER                              = LoggerFactory.getLogger(ReqReportBuilderExcel.class);

    private static final String LOGGER_PREFIX_TAG                   = String.format("%-15s -", "[ExcelReport]");

    private static final int    HEADERS_ROW_POSITION                = 0;

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
            final List<String> versions = getRequirements().stream().map(Requirement::getVersion).distinct().collect(Collectors.toList());
            Collections.sort(versions);

            int summaryCurrentRowIndex = 5;

            this.createOrUpdateSummarySheet(workbook, null, summaryCurrentRowIndex);

            for (final String version : versions) {

                final Collection<Requirement> reqs = Requirements.getByVersion(getRequirements(), version).stream().sorted().collect(Collectors.toList());

                this.createOrUpdateSummarySheet(workbook, version, summaryCurrentRowIndex);
                summaryCurrentRowIndex += 6;

                final Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Version " + version));

                sheet.setZoom(DEFAULT_ZOOM_SCALE);

                final PrintSetup ps = sheet.getPrintSetup();

                sheet.setAutobreaks(true);

                ps.setFitHeight((short) 1);
                ps.setFitWidth((short) 1);

                this.createHeaders(workbook, sheet);

                int rowIndex = HEADERS_ROW_POSITION + 1;
                for (final Requirement req : reqs) {
                    final Row row = sheet.createRow(rowIndex++);
                    this.populateRow(workbook, row, req);
                }

                final Footer footer = sheet.getFooter();
                footer.setRight("Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages());
            }

            LOGGER.trace("{} Creating the .xlsx file -> {}", LOGGER_PREFIX_TAG, excelOutputFile);
            workbook.write(out);
            LOGGER.info("{} Finished generating EXCEL report", LOGGER_PREFIX_TAG);

        } catch (Exception e) {
            String errMsg = "Error while building the EXCEL coverage report : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqReportBuilderException(errMsg, e);
        }

    }

    private void createOrUpdateSummarySheet(final Workbook wb, final String version, final int rowStartPosition) {

        Sheet sheet = wb.getSheet("Summary");

        if (sheet == null) {
            sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("Summary"));

            final CreationHelper creationHelper = wb.getCreationHelper();
            final Font font = wb.createFont();
            XSSFCellStyle cellStyle = (XSSFCellStyle) wb.createCellStyle();

            // Title
            final Row rowTitle = sheet.createRow(0);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            font.setBold(true);
            cellStyle.setFont(font);

            final Cell cellTitle = rowTitle.createCell(4);
            cellTitle.setCellValue(creationHelper.createRichTextString(getDefaultReportConfig().getTitle()));
            cellTitle.setCellStyle(cellStyle);
            sheet.autoSizeColumn(cellTitle.getColumnIndex());

            // Row for total of requirements for all versions
            final Row rowTotalOfReqsForAllVersions = sheet.createRow(3);
            final Cell cellTotalOfReqs = rowTotalOfReqsForAllVersions.createCell(0);
            cellTotalOfReqs.setCellValue(creationHelper.createRichTextString("Total of requirements : " + getRequirements().size()));
            cellTotalOfReqs.getCellStyle().setFont(font);
            sheet.autoSizeColumn(cellTotalOfReqs.getColumnIndex());

            sheet.setZoom(DEFAULT_ZOOM_SCALE);
        }

        if (!StringUtils.isBlank(version)) {
            this.updateSummarySheet(wb, sheet, version, rowStartPosition);
        }
    }

    private void createHeaders(final Workbook wb, final Sheet sheet) {

        final Row row = sheet.createRow(HEADERS_ROW_POSITION);

        final XSSFCellStyle cellStyle = (XSSFCellStyle) wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(Color.LIGHT_GRAY));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        final Font headerFont = wb.createFont();
        headerFont.setBold(true);
        cellStyle.setFont(headerFont);

        final CreationHelper creationHelper = wb.getCreationHelper();

        for (final CellConfig cellConfig : CellConfig.values()) {
            final Cell cell = row.createCell(cellConfig.position);
            cell.setCellValue(creationHelper.createRichTextString(cellConfig.header));
            addBordersToCellStyle(cellStyle, BorderStyle.MEDIUM);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(cellConfig.position);
        }
    }

    private void updateSummarySheet(final Workbook wb, final Sheet sheet, final String version, final int rowStartPosition) {

        // Source Code Coverage
        this.createCodeCoverageSummary(wb, sheet, rowStartPosition, CODE_TYPE.SOURCE, version);

        // Test Code Coverage
        this.createCodeCoverageSummary(wb, sheet, rowStartPosition, CODE_TYPE.TEST, version);
    }

    private void createCodeCoverageSummary(final Workbook wb, final Sheet sheet, final int rowStartPosition, final CODE_TYPE codeType, final String version) {

        final CreationHelper creationHelper = wb.getCreationHelper();
        final XSSFCellStyle cellStyle = (XSSFCellStyle) wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(Color.LIGHT_GRAY));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBordersToCellStyle(cellStyle, BorderStyle.MEDIUM);
        final Font font = wb.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        final XSSFCellStyle cellStyleTitle = (XSSFCellStyle) wb.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        cellStyleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBordersToCellStyle(cellStyleTitle, BorderStyle.MEDIUM);
        cellStyleTitle.setFont(font);

        // Version row part ...
        final XSSFCellStyle cellStyleVersion = (XSSFCellStyle) wb.createCellStyle();
        cellStyleVersion.setAlignment(HorizontalAlignment.CENTER);
        cellStyleVersion.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        cellStyleVersion.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyleVersion.setBorderTop(BorderStyle.MEDIUM);
        cellStyleVersion.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleVersion.setFont(font);
        Row rowVersion = sheet.getRow(rowStartPosition);
        if (rowVersion == null) {
            rowVersion = sheet.createRow(rowStartPosition);
        }
        for (int i = 0; i < 9; i++) {
            final Cell cell = rowVersion.createCell(i);
            cell.setCellStyle(cellStyleVersion);
        }
        final Cell cellVersion = rowVersion.getCell(4);
        cellVersion.setCellValue(creationHelper.createRichTextString("Version " + version));
        sheet.autoSizeColumn(cellVersion.getColumnIndex());

        int columnOffset = 0;

        // Title part ...
        Row rowCodeCoverageTitle = sheet.getRow(rowStartPosition + 1);
        if (rowCodeCoverageTitle != null) {
            columnOffset += 5;
        } else {
            rowCodeCoverageTitle = sheet.createRow(rowStartPosition + 1);
        }
        final Cell cellCodeCoverateTitle = rowCodeCoverageTitle.createCell(columnOffset);
        if (CODE_TYPE.SOURCE == codeType) {
            cellCodeCoverateTitle.setCellValue(creationHelper.createRichTextString(getDefaultReportConfig().getCodeCoverageDiagramName()));
        } else {
            cellCodeCoverateTitle.setCellValue(creationHelper.createRichTextString(getDefaultReportConfig().getTestsCoverageDiagramName()));
        }
        cellCodeCoverateTitle.setCellStyle(cellStyleTitle);
        // End of title part ...

        final Cell cellCodeDoneTitle = rowCodeCoverageTitle.createCell(columnOffset + 1);
        cellCodeDoneTitle.setCellValue(creationHelper.createRichTextString("Done"));
        cellCodeDoneTitle.setCellStyle(cellStyle);

        final Cell cellCodeUndoneTitle = rowCodeCoverageTitle.createCell(columnOffset + 2);
        cellCodeUndoneTitle.setCellValue(creationHelper.createRichTextString("Undone"));
        cellCodeUndoneTitle.setCellStyle(cellStyle);

        final Cell cellReqIgnoredTitle = rowCodeCoverageTitle.createCell(columnOffset + 3);
        cellReqIgnoredTitle.setCellValue(creationHelper.createRichTextString("Ignored"));
        cellReqIgnoredTitle.setCellStyle(cellStyle);

        Row rowTotalCount = sheet.getRow(rowStartPosition + 2);
        if (rowTotalCount == null) {
            rowTotalCount = sheet.createRow(rowStartPosition + 2);
        }
        final Cell cellCountTitle = rowTotalCount.createCell(columnOffset);
        cellCountTitle.setCellStyle(cellStyle);
        cellCountTitle.setCellValue(creationHelper.createRichTextString("Total"));

        Row rowPercentage = sheet.getRow(rowStartPosition + 3);
        if (rowPercentage == null) {
            rowPercentage = sheet.createRow(rowStartPosition + 3);
        }
        final Cell cellPercentageTitle = rowPercentage.createCell(columnOffset);
        cellPercentageTitle.setCellStyle(cellStyle);
        cellPercentageTitle.setCellValue(creationHelper.createRichTextString("%"));

        // Done part ...
        final XSSFCellStyle cellStyleDone = (XSSFCellStyle) wb.createCellStyle();
        cellStyleDone.setAlignment(HorizontalAlignment.CENTER);
        cellStyleDone.setFillForegroundColor(new XSSFColor(Color.GREEN));
        cellStyleDone.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBordersToCellStyle(cellStyleDone, BorderStyle.THIN);

        final Cell cellDoneCount = rowTotalCount.createCell(columnOffset + 1);
        cellDoneCount.setCellStyle(cellStyleDone);
        if (CODE_TYPE.SOURCE == codeType) {
            final long codeDoneCount = getCodeDoneCount(version);
            cellDoneCount.setCellValue(creationHelper.createRichTextString(String.valueOf(codeDoneCount)));
        } else if (CODE_TYPE.TEST == codeType) {
            final long testsDoneCount = getTestsDoneCount(version);
            cellDoneCount.setCellValue(creationHelper.createRichTextString(String.valueOf(testsDoneCount)));
        }

        final Cell cellDonePercentage = rowPercentage.createCell(columnOffset + 1);
        cellDonePercentage.setCellStyle(cellStyleDone);
        if (CODE_TYPE.SOURCE == codeType) {
            cellDonePercentage.setCellValue(creationHelper.createRichTextString(formatRatioIntoPercentage(getCodeDoneRatio(version))));
        } else if (CODE_TYPE.TEST == codeType) {
            cellDonePercentage.setCellValue(creationHelper.createRichTextString(formatRatioIntoPercentage(getTestDoneRatio(version))));
        }

        // Undone part ...
        boolean setColor = false;

        final XSSFCellStyle cellStyleUndone = (XSSFCellStyle) wb.createCellStyle();
        cellStyleUndone.setAlignment(HorizontalAlignment.CENTER);
        addBordersToCellStyle(cellStyleUndone, BorderStyle.THIN);

        final Cell cellUndoneCount = rowTotalCount.createCell(columnOffset + 2);
        cellUndoneCount.setCellStyle(cellStyleUndone);
        if (CODE_TYPE.SOURCE == codeType) {
            final long codeUndoneCount = getCodeUndoneCount(version);
            setColor = codeUndoneCount > 0;
            cellUndoneCount.setCellValue(creationHelper.createRichTextString(String.valueOf(codeUndoneCount)));
        } else if (CODE_TYPE.TEST == codeType) {
            final long testsUndoneCount = getTestsUndoneCount(version);
            setColor = testsUndoneCount > 0;
            cellUndoneCount.setCellValue(creationHelper.createRichTextString(String.valueOf(testsUndoneCount)));
        }
        if (setColor) {
            cellStyleUndone.setFillForegroundColor(new XSSFColor(Color.RED));
            cellStyleUndone.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        final Cell cellUndonePercentage = rowPercentage.createCell(columnOffset + 2);
        cellUndonePercentage.setCellStyle(cellStyleUndone);
        if (CODE_TYPE.SOURCE == codeType) {
            cellUndonePercentage
                    .setCellValue(creationHelper.createRichTextString(formatRatioIntoPercentage(1f - getCodeDoneRatio(version) - this.getRequirementsIgnoredRatio(version))));
        } else if (CODE_TYPE.TEST == codeType) {
            cellUndonePercentage
                    .setCellValue(creationHelper.createRichTextString(formatRatioIntoPercentage(1f - getTestDoneRatio(version) - this.getRequirementsIgnoredRatio(version))));
        }

        // Ignored requirements part ...
        final XSSFCellStyle cellStyleIgnore = (XSSFCellStyle) wb.createCellStyle();
        cellStyleIgnore.setAlignment(HorizontalAlignment.CENTER);
        addBordersToCellStyle(cellStyleIgnore, BorderStyle.THIN);

        final Cell cellIgnoreCount = rowTotalCount.createCell(columnOffset + 3);
        cellIgnoreCount.setCellStyle(cellStyleIgnore);
        long ignoredRequirementsCount = getIgnoredRequirementsCount(version);
        setColor = ignoredRequirementsCount > 0;
        if (setColor) {
            cellStyleIgnore.setFillForegroundColor(new XSSFColor(Color.YELLOW));
            cellStyleIgnore.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        cellIgnoreCount.setCellValue(creationHelper.createRichTextString(String.valueOf(ignoredRequirementsCount)));

        final Cell cellIgnorePercentage = rowPercentage.createCell(columnOffset + 3);
        cellIgnorePercentage.setCellStyle(cellStyleIgnore);
        cellIgnorePercentage.setCellValue(creationHelper.createRichTextString(formatRatioIntoPercentage(this.getRequirementsIgnoredRatio(version))));

        sheet.autoSizeColumn(columnOffset);
    }

    private String formatRatioIntoPercentage(final float ratio) {
        return new DecimalFormat("##.#").format(ratio * 100) + " %";
    }

    private float getRequirementsIgnoredRatio(final String version) {
        return 1f * getIgnoredRequirementsCount(version) / getRequirementByVersion(version).size();
    }

    private void populateRow(final Workbook wb, final Row row, final Requirement req) {

        final Sheet currentSheet = row.getSheet();

        for (final CellConfig cellConfig : CellConfig.values()) {

            final Cell cell = row.createCell(cellConfig.position);
            final Cell headerCell = currentSheet.getRow(HEADERS_ROW_POSITION).getCell(cellConfig.position);
            final CellStyle headerCellStyle = headerCell.getCellStyle();

            final Font cellFont = wb.createFont();

            final CreationHelper creationHelper = wb.getCreationHelper();

            String cellContent = getRequirementFieldValue(cellConfig.position, req);

            XSSFColor cellColor = new XSSFColor(Color.WHITE);

            switch (cellConfig) {
            case CODE:
                cellColor = getColorForDoneCell(req.isIgnore(), req.isCodeDone());
                break;

            case TEST:
                cellColor = getColorForDoneCell(req.isIgnore(), req.isTestDone());
                break;

            case LINK:
                if (!StringUtils.isBlank(cellContent)) {
                    configureHyperlinkCell(cell, cellFont, creationHelper, cellContent);
                    cellContent = "Link";
                }
                break;

            default:
                break;
            }

            cell.setCellValue(creationHelper.createRichTextString(cellContent));

            final XSSFCellStyle currentCellStyle = (XSSFCellStyle) wb.createCellStyle();
            currentCellStyle.setFillForegroundColor(cellColor);
            currentCellStyle.setFont(cellFont);
            addBordersToCellStyle(currentCellStyle, BorderStyle.THIN);
            cell.setCellStyle(currentCellStyle);
            currentCellStyle.setAlignment(headerCellStyle.getAlignmentEnum());
            currentCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            if (req.isIgnore() && !(CellConfig.CODE == cellConfig || CellConfig.TEST == cellConfig)) {
                currentCellStyle.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
                currentCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
            }

            currentSheet.autoSizeColumn(cellConfig.position);
        }
    }

    private void configureHyperlinkCell(final Cell cell, final Font cellFont, final CreationHelper creationHelper, String cellContent) {
        cellFont.setUnderline(Font.U_SINGLE);
        cellFont.setColor(IndexedColors.BLUE.getIndex());
        final Hyperlink link = creationHelper.createHyperlink(HyperlinkType.URL);
        link.setAddress(cellContent);
        cell.setHyperlink(link);
    }

    private static void addBordersToCellStyle(final XSSFCellStyle currentCellStyle, final BorderStyle borderStyle) {
        currentCellStyle.setBorderTop(borderStyle);
        currentCellStyle.setBorderBottom(borderStyle);
        currentCellStyle.setBorderLeft(borderStyle);
        currentCellStyle.setBorderRight(borderStyle);
    }

    private static XSSFColor getColorForDoneCell(final boolean ignore, final boolean done) {
        if (ignore) {
            return new XSSFColor(Color.YELLOW);
        } else {
            return done ? new XSSFColor(Color.GREEN) : new XSSFColor(Color.RED);
        }
    }

    private String getRequirementFieldValue(final int position, final Requirement requirement) {
        switch (position) {
        case 0:
            return requirement.getName();
        case 1:
            return requirement.getVersion();
        case 2:
            return requirement.getRevision();
        case 3:
            return requirement.getShortDescription();
        case 4:
            final String codeDoneMsg = requirement.isCodeDone() ? "OK" : "KO";
            return requirement.isIgnore() ? "Ignore" : codeDoneMsg;
        case 5:
            return requirement.getCodeAuthor();
        case 6:
            final String testDoneMsg = requirement.isTestDone() ? "OK" : "KO";
            return requirement.isIgnore() ? "Ignore" : testDoneMsg;
        case 7:
            return requirement.getTestAuthor();
        case 8:
            return requirement.getLink();
        default:
            throw new IllegalStateException("The position " + position + " is not handled for the Excel report !!!");
        }
    }

    @Override
    protected String getDefaultFileReportExtension() {
        return EXCEL_REPORT_FILE_DEFAULT_EXTENSION;
    }

    private enum CODE_TYPE {
                            SOURCE,
                            TEST;
    }

}
