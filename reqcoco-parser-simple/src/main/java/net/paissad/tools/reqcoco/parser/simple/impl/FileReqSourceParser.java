package net.paissad.tools.reqcoco.parser.simple.impl;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqSourceParserException;

public class FileReqSourceParser extends AbstractReqSourceParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileReqSourceParser.class);

	/**
	 * Can be used when the {@link URI} is a link to a file.
	 */
	@Override
	public Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig declTagConfig, final Map<String, Object> options)
	        throws ReqSourceParserException {

		try (final Stream<String> lines = Files.lines(Paths.get(uri), StandardCharsets.UTF_8)) {

			final Set<Requirement> declaredRequirements = new HashSet<>();

			final Predicate<String> lineContainsRequirementPredicate = Pattern.compile(declTagConfig.getCompleteRegex()).asPredicate();

			lines.parallel().filter(lineContainsRequirementPredicate)
			        .forEach(line -> declaredRequirements.addAll(getRequirementsFromString(declTagConfig, line)));

			return declaredRequirements;

		} catch (Exception e) {
			String errMsg = "I/O error while parsing the source for retrieving the declared requirements : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqSourceParserException(errMsg, e);
		}
	}

}
