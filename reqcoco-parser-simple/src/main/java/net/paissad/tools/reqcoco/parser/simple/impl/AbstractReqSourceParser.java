package net.paissad.tools.reqcoco.parser.simple.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.parser.simple.util.ReqTagUtil;

public abstract class AbstractReqSourceParser implements ReqSourceParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReqSourceParser.class);

	/**
	 * @param declTagConfig - Configuration to use for retrieving the requirement tags.
	 * @param text - The text to parse
	 * @return The requirement extracted from the specified text content.
	 */
	protected Collection<Requirement> getRequirementsFromString(final ReqDeclTagConfig declTagConfig, final String text) {

		final Collection<Requirement> extractedRequirements = new HashSet<>();

		final Pattern patternTag = Pattern.compile(declTagConfig.getCompleteRegex());
		final Matcher matcherTag = patternTag.matcher(ReqTagUtil.unEscapeString(text));

		while (matcherTag.find()) {

			final String tag = matcherTag.group();

			// Retrieve the 'id' part of the tag
			String id = ReqTagUtil.trimString(ReqTagUtil.extractFieldValue(tag, declTagConfig.getIdRegex(), 1));
			if (StringUtils.isBlank(id)) {
				LOGGER.error("No id defined for requirement tag --> {}", tag);
			}

			// Retrieve the 'version' part of the tag
			String version = ReqTagUtil.trimString(ReqTagUtil.extractFieldValue(tag, declTagConfig.getVersionRegex(), 1));
			if (StringUtils.isBlank(version)) {
				LOGGER.warn("No version defined for tag --> {} <--- Version is going to be set to '{}'", tag, Requirement.VERSION_UNKNOWN);
				version = Requirement.VERSION_UNKNOWN;
			}

			// Retrieve the 'revision' part of the tag
			final String revision = ReqTagUtil.trimString(ReqTagUtil.extractFieldValue(tag, declTagConfig.getRevisionRegex(), 1));

			// Retrieve the 'summary' part of the tag
			final String summary = ReqTagUtil.trimString(ReqTagUtil.extractFieldValue(tag, declTagConfig.getSummaryRegex(), 1));

			final Requirement req = new Requirement(id, version, revision);
			req.setShortDescription(summary);
			extractedRequirements.add(req);
		}

		return extractedRequirements;
	}

}
