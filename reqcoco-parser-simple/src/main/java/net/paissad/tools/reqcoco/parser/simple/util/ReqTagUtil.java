package net.paissad.tools.reqcoco.parser.simple.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;

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
     * @param str - The string to unescape.
     * @return The HTML and XML unescaped string.
     */
    public static String unEscapeString(final String str) {
        return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeXml(str));
    }

    /**
     * @param str - The string from which to strip the tag.
     * @param tagConfig - The tag configuration
     * @return The string without the tag content, and trimmed afterwards.
     */
    public static String stripTagAndTrim(final String str, final ReqDeclTagConfig tagConfig) {
        return str.replaceAll(tagConfig.getCompleteRegex(), "").trim();
    }

}
