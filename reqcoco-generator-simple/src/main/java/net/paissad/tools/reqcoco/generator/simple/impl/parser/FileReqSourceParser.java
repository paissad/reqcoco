package net.paissad.tools.reqcoco.generator.simple.impl.parser;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.generator.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.generator.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.generator.simple.util.ReqTagUtil;

public class FileReqSourceParser implements ReqSourceParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileReqSourceParser.class);

	/**
	 * Can be used when the {@link URI} is a link to a file.
	 */
	@Override
	public Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig declTagConfig, final Map<String, Object> options)
	        throws ReqSourceParserException {

		try (final Stream<String> lines = Files.lines(Paths.get(uri), StandardCharsets.UTF_8)) {

			final Set<Requirement> declaredRequirements = new HashSet<>();

			final Pattern patternTag = Pattern.compile(declTagConfig.getCompleteRegex());

			lines.parallel().filter(patternTag.asPredicate()).forEach(line -> {

				final Matcher matcherTag = patternTag.matcher(line);
				while (matcherTag.find()) {

					final String tag = matcherTag.group();

					// Retrieve the 'id' part of the tag
					final String id = ReqTagUtil.extractFieldValue(tag, declTagConfig.getIdRegex(), 1);
					if (StringUtils.isBlank(id)) {
						LOGGER.error("No id defined for requirement tag --> {}", tag);
					}

					// Retrieve the 'version' part of the tag
					String version = ReqTagUtil.extractFieldValue(tag, declTagConfig.getVersionRegex(), 1);
					if (StringUtils.isBlank(version)) {
						LOGGER.warn("No version defined for tag --> {} <--- Version is going to be set to '{}'", tag, Requirement.VERSION_UNKNOWN);
						version = Requirement.VERSION_UNKNOWN;
					}

					// Retrieve the 'revision' part of the tag
					final String revision = ReqTagUtil.extractFieldValue(tag, declTagConfig.getRevisionRegex(), 1);

					// Retrieve the 'summary' part of the tag
					final String summary = ReqTagUtil.extractFieldValue(tag, declTagConfig.getSummaryRegex(), 1);

					final Requirement req = new Requirement(id, version, revision);
					req.setShortDescription(summary);
					declaredRequirements.add(req);
				}
			});

			return declaredRequirements;

		} catch (IOException e) {
			String errMsg = "I/O error while parsing the source for retrieving the declared requirements : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqSourceParserException(errMsg, e);
		}
	}

}
