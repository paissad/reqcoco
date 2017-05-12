package net.paissad.tools.reqcoco.generator.simple.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		// return str == null ? null : str.replaceAll("^[\\u00A0|\\s]*(.*?)[\\u00A0|\\s]*$", "$1");
		return str == null ? null : str.replaceAll("^[\\h|\\s]*(.*?)[\\h|\\s]*$", "$1");
	}

}
