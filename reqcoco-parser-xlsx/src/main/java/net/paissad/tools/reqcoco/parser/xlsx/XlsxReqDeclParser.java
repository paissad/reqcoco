package net.paissad.tools.reqcoco.parser.xlsx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqParserException;
import net.paissad.tools.reqcoco.parser.simple.spi.ReqDeclParser;
import net.paissad.tools.reqcoco.parser.simple.util.ReqTagUtil;

public class XlsxReqDeclParser implements ReqDeclParser {

    private static final Logger LOGGER            = LoggerFactory.getLogger(XlsxReqDeclParser.class);

    public static final String  PARSER_IDENTIFIER = "XLSX";

    @Override
    public String getIdentitier() {
        return PARSER_IDENTIFIER;
    }

	@Override
	public Collection<Requirement> parse(URI uri, ReqDeclTagConfig declTagConfig, Map<String, Object> options) throws ReqParserException {

		try (final InputStream in = Files.newInputStream(Paths.get(uri)); final XSSFWorkbook workbook = new XSSFWorkbook(in)) {

			final Set<Requirement> declaredRequirements = new HashSet<>();

			LOGGER.debug("Iterating through all sheets of the xlsx file -> {}", uri);
			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {

				final XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				LOGGER.debug("Opening sheet '{}'", sheet.getSheetName());

				LOGGER.trace("Iterating through all rows of the current sheet '{}' and skipping the first row which represents the headers",
				        sheet.getSheetName());

				Map<String, Integer> headersPositions = null;
				int rowIndex = 0;

				final Iterator<Row> rowIterator = sheet.iterator();
				while (rowIterator.hasNext()) {

					final Row row = rowIterator.next();

					if (rowIndex == 0) {
						headersPositions = this.getCellHeadersPositions(row);

					} else {

                        final String name = ReqTagUtil.trimString(row.getCell(headersPositions.get("name")).getStringCellValue());
						final String version = ReqTagUtil.trimString(row.getCell(headersPositions.get("version")).getStringCellValue());

						String revision = null;
                        if (headersPositions.get("revision") != null) {
                            revision = ReqTagUtil.trimString(row.getCell(headersPositions.get("revision")).getStringCellValue());    
                        }

                        final Requirement req = new Requirement(name, version, revision);

                        if (headersPositions.get("group") != null) {
                            final String group = ReqTagUtil.trimString(row.getCell(headersPositions.get("group")).getStringCellValue());
                            req.setGroup(group);
                        }
                        
                        if (headersPositions.get("summary") != null) {
                            final String summary = ReqTagUtil.trimString(row.getCell(headersPositions.get("summary")).getStringCellValue());
                            req.setShortDescription(summary);
                        }

                        if (headersPositions.get("link") != null) {
                            final String link = ReqTagUtil.trimString(row.getCell(headersPositions.get("link")).getStringCellValue());
                            req.setLink(link);
                        }

						declaredRequirements.add(req);
					}

					rowIndex++;
				}
			}

			return declaredRequirements;

		} catch (IOException e) {
			String errMsg = "Error while reading the xlsx file : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqParserException(errMsg, e);
		}

	}

    @Override
    public Collection<String> getRegisteredFileExtensions() {
        return Arrays.asList(".xlsx");
    }

	private Map<String, Integer> getCellHeadersPositions(final Row row) {
		final Map<String, Integer> headers = new HashMap<>();
		final Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			final Cell cell = cellIterator.next();
			final String name = cell.getStringCellValue();
			final Integer position = cell.getColumnIndex();
			headers.put(name.toLowerCase(Locale.US), position);
		}
		return headers;
	}

}
