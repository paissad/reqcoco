package net.paissad.tools.reqcoco.parser.docx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.parser.simple.impl.AbstractReqSourceParser;

public class DocxReqSourceParser extends AbstractReqSourceParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocxReqSourceParser.class);

	@Override
	public Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig declTagConfig, final Map<String, Object> options)
	        throws ReqSourceParserException {

		try (final InputStream in = Files.newInputStream(Paths.get(uri)); final XWPFDocument document = new XWPFDocument(in)) {

			final Set<Requirement> declaredRequirements = new HashSet<>();

			final Predicate<String> textContainsRequirementPredicate = Pattern.compile(declTagConfig.getCompleteRegex()).asPredicate();

			LOGGER.debug("Retrieving all paragraphs contained into the docx file -> {}", uri);
			document.getParagraphs().stream().map(XWPFParagraph::getText).filter(textContainsRequirementPredicate)
			        .forEach(text -> declaredRequirements.addAll(getRequirementsFromString(declTagConfig, text)));

			return declaredRequirements;

		} catch (IOException e) {
			String errMsg = "Error while reading the docx file : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqSourceParserException(errMsg, e);
		}
	}

}
