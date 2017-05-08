package net.paissad.tools.reqcoco.generator.redmine.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.taskadapter.redmineapi.RedmineException;

import net.paissad.tools.reqcoco.generator.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.generator.simple.impl.tag.SimpleReqDeclTagConfig;

public class RedmineReqSourceParserTest {

	private RedmineReqSourceParser	redmineReqSourceParser;

	private URI						stubUri;

	private ReqDeclTagConfig		tagConfig;

	private Map<String, Object>		options;

	@Rule
	public ExpectedException		thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.redmineReqSourceParser = new RedmineReqSourceParser();
		this.stubUri = new URI("http://localhost:3000/");
		this.tagConfig = new SimpleReqDeclTagConfig();
		this.initOptions();
	}

	@Test
	@Ignore(value = "This test need to configure a Redmine server before ...")
	public void testParse() throws ReqSourceParserException {

		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWhenUriIsNull() throws ReqSourceParserException {

		this.stubUri = null;
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("The root URL of Redmine cannot be null !");
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWithEmptyOptions() throws ReqSourceParserException {

		this.options = Collections.emptyMap();
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("Non null and non empty options must be passed for in order to parse a Redmine project");
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWithNullOptions() throws ReqSourceParserException {

		this.options = null;
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("Non null and non empty options must be passed for in order to parse a Redmine project");
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWhenProjectIdIsAbsent() throws ReqSourceParserException {

		this.options.remove(RedmineReqSourceParser.OPTION_PROJECT_KEY);
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("A non null project id or name must be provided for parsing requirements from Redmine");
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWhenProjectIdIsBlank() throws ReqSourceParserException {

		this.options.put(RedmineReqSourceParser.OPTION_PROJECT_KEY, "");
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("A non null project id or name must be provided for parsing requirements from Redmine");
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWhenProjectOneOptionHasBadType() throws ReqSourceParserException {

		this.options.put(RedmineReqSourceParser.OPTION_TARGET_VERSIONS, "v1.9");
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("Error either while retrieving options, or while processing the API result");
		thrown.expectCause(Is.isA(ClassCastException.class));
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWhenUriIsUnreachable() throws ReqSourceParserException, URISyntaxException {

		this.stubUri = new URI("http://localhost:3000/for_sure_id_dont_exist");
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("Error while retrieving redmine issues : ");
		thrown.expectCause(Is.isA(RedmineException.class));
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	private void initOptions() {
		this.options = new HashMap<>();
		this.options.put(RedmineReqSourceParser.OPTION_AUTH_API_KEY, "7ac3db9bcf094e273dc2c6ec34ddf2c56862f760");
		this.options.put(RedmineReqSourceParser.OPTION_PROJECT_KEY, "dumm1");
		this.options.put(RedmineReqSourceParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, false);
		// this.options.put(RedmineReqSourceParser.OPTION_TRACKER_FILTER, "1");
		this.options.put(RedmineReqSourceParser.OPTION_STATUS_FILTER, "*");
		this.options.put(RedmineReqSourceParser.OPTION_TARGET_VERSIONS, Arrays.asList(new String[] {}));
		this.options.put(RedmineReqSourceParser.OPTION_INCLUDE_CHILDREN, true);
		this.options.put(RedmineReqSourceParser.OPTION_INCLUDE_RELATIONS, true);
	}

}
