package net.paissad.tools.reqcoco.parser.xlsx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import net.paissad.tools.reqcoco.parser.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.parser.simple.util.ReqTagUtil;

public class XlsxReqSourceParser implements ReqSourceParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(XlsxReqSourceParser.class);

	@Override
	public Collection<Requirement> parse(URI uri, ReqDeclTagConfig declTagConfig, Map<String, Object> options) throws ReqSourceParserException {

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
						final String revision = ReqTagUtil.trimString(row.getCell(headersPositions.get("revision")).getStringCellValue());
						final String summary = ReqTagUtil.trimString(row.getCell(headersPositions.get("summary")).getStringCellValue());

						final Map<String, String> tagMembers = new HashMap<>();
						tagMembers.put("name", name);
						tagMembers.put("version", version);
						tagMembers.put("revision", revision);
						tagMembers.put("summary", summary);

						final Requirement req = new Requirement(name, version, revision);
						req.setShortDescription(summary);

						declaredRequirements.add(req);
					}

					rowIndex++;
				}
			}

			return declaredRequirements;

		} catch (IOException e) {
			String errMsg = "Error while reading the xlsx file : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqSourceParserException(errMsg, e);
		}

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
