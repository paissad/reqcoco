package net.paissad.tools.reqcoco.generator.simple.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.paissad.tools.reqcoco.generator.simple.api.ReqDeclTag;

public interface ReqTagUtil {

	/**
	 * @param input - The input to process.
	 * @param regex - The regexp to use.
	 * @param groupIndex - The group index.
	 * @return The value of the group retrieved by using the specified regexp, <code>null</code> if not match.
	 */
	public static String extractFieldValue(final String input, final String regex, final int groupIndex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(groupIndex);
		}
		return null;
	}

	public static String trimString(final String str) {
		return str == null ? null : str.replaceAll("^[\\h|\\s]*(.*?)[\\h|\\s]*$", "$1");
	}

	/**
	 * @param tagMembers - The map containing the values to use for building the {@link ReqDeclTag}.
	 * @return A new instance of {@link ReqDeclTag} built from the specified map.
	 */
	public static ReqDeclTag buildReqDeclTagFromMembers(final Map<String, String> tagMembers) {

		final ReqDeclTag tag = new ReqDeclTag();

		tag.setId(tagMembers.get("id"));
		tag.setVersion(tagMembers.get("version"));
		tag.setRevision(tagMembers.get("revision"));
		tag.setSummary(tagMembers.get("summary"));

		return tag;
	}

}
