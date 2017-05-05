package net.paissad.tools.reqcoco.generator.simple.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReqTagUtil {

	private ReqTagUtil() {
	}

	// TODO : write unit tests !
	/**
	 * @param input - The input to process.
	 * @param regex - The regexp to use.
	 * @param groupIndex - The group index.
	 * @return The value of the group retrieved by using the specified regexp.
	 */
	public static String extractFieldValue(final String input, final String regex, final int groupIndex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(groupIndex);
		}
		return null;
	}

}
