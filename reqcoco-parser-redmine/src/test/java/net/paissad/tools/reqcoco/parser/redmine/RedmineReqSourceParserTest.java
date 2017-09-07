package net.paissad.tools.reqcoco.parser.redmine;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.taskadapter.redmineapi.RedmineException;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;

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
		this.stubUri = new URI("https://www.redmine.org");
		// this.stubUri = new URI("http://localhost:3000");
		this.tagConfig = new SimpleReqDeclTagConfig();
		this.initOptions();
	}

	@Test
	public void testParse() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertFalse(reqs.isEmpty());
	}

	@Test
	public void testParseWithMatchReqTagSetToTrue() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();
		this.options.put(RedmineReqSourceParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, true);
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertTrue(reqs.isEmpty()); // Hope that no one will use the tag '@Req()' ;-)
	}

	@Test
	public void testParseWhenIncludeChildrenIsSetToFalse() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();
		this.options.put(RedmineReqSourceParser.OPTION_INCLUDE_CHILDREN, false);
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertFalse(reqs.isEmpty());
	}

	@Test
	public void testParseWhenIncludeRelationsIsSetToFalse() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();
		this.options.put(RedmineReqSourceParser.OPTION_INCLUDE_RELATIONS, false);
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertFalse(reqs.isEmpty());
	}

	@Test
	public void testParseWhenTargetVersionIsNotEmpty() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();
		this.options.put(RedmineReqSourceParser.OPTION_TARGET_VERSIONS, Arrays.asList(new String[] { "9.8.7.6" }));
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertTrue(reqs.isEmpty());
	}

	@Test
	public void testParseWhenExtraPropertiesIsEmpty() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();

		this.options.put(RedmineReqSourceParser.OPTION_TRACKER_FILTER, "99"); // Sure this tracker won't exit so that we don't waste retrieve too many
		                                                                      // issues ...
		this.options.put(RedmineReqSourceParser.OPTION_EXTRA_PROPERTIES, new Properties());
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertTrue(reqs.isEmpty());
	}

	@Test
	public void testParseWhenTrackerFilterIsNotSet() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();

		this.options.remove(RedmineReqSourceParser.OPTION_TRACKER_FILTER);
		final Properties extraProps = new Properties();
		extraProps.put("assigned_to_id", "0"); // User with id '0' does not exit ... the request should run fast
		this.options.put(RedmineReqSourceParser.OPTION_EXTRA_PROPERTIES, extraProps);
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertTrue(reqs.isEmpty());
	}

	@Test
	public void testParseWithApiAccessKeyAuth() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();

		this.options.put(RedmineReqSourceParser.OPTION_AUTH_API_KEY, "azertyuiopsdfghjk"); // Since redmine.org does not need any authentication,
		                                                                                   // the api access key is not checked actually
		final Properties extraProps = new Properties();
		extraProps.put("assigned_to_id", "0"); // User with id '0' does not exit ... the request should run fast
		this.options.put(RedmineReqSourceParser.OPTION_EXTRA_PROPERTIES, extraProps);
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertTrue(reqs.isEmpty());
	}

	@Test
	public void testParseWithUsernameAndPasswordAuth() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();

		this.options.put(RedmineReqSourceParser.OPTION_AUTH_USER_NAME, "my_user"); // Since redmine.org does not need any authentication, the
		                                                                           // user/pass is not checked actually
		this.options.put(RedmineReqSourceParser.OPTION_AUTH_USER_PASS, "my_pass");
		final Properties extraProps = new Properties();
		extraProps.put("assigned_to_id", "0"); // User with id '0' does not exit ... the request should run fast
		this.options.put(RedmineReqSourceParser.OPTION_EXTRA_PROPERTIES, extraProps);
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertTrue(reqs.isEmpty());
	}

	@Test
	public void testParseWhenAllIncludesAreSetToFalse() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();

		this.options.put(RedmineReqSourceParser.OPTION_INCLUDE_CHILDREN, false);
		this.options.put(RedmineReqSourceParser.OPTION_INCLUDE_RELATIONS, false);
		final Collection<Requirement> reqs = this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
		Assert.assertFalse(reqs.isEmpty());
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
		thrown.expectMessage("Non null and non empty options must be passed in order to parse a Redmine project");
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWithNullOptions() throws ReqSourceParserException {

		this.options = null;
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("Non null and non empty options must be passed in order to parse a Redmine project");
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

		RedmineTestUtil.assumeRedminePublicApiReachable();

		this.options.put(RedmineReqSourceParser.OPTION_PROJECT_KEY, "");
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("A non null project id or name must be provided for parsing requirements from Redmine");
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWhenProjectOneOptionHasBadType() throws ReqSourceParserException {

		RedmineTestUtil.assumeRedminePublicApiReachable();
		this.options.put(RedmineReqSourceParser.OPTION_TARGET_VERSIONS, "v1.9");
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("Error either while retrieving options, or while processing the API result");
		thrown.expectCause(Is.isA(ClassCastException.class));
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}

	@Test
	public void testParseWhenUriIsUnreachable() throws ReqSourceParserException, URISyntaxException {

		this.stubUri = new URI("http://localhost:3000/for_sure_i_dont_exist");
		thrown.expect(ReqSourceParserException.class);
		thrown.expectMessage("Error while retrieving Redmine issues : ");
		thrown.expectCause(Is.isA(RedmineException.class));
		this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
	}
	
    @Test
    public void testIncompatibilityOfSubjectTagAndDeclarationCustomField() throws ReqSourceParserException {

        RedmineTestUtil.assumeRedminePublicApiReachable();
        this.options.put(RedmineReqSourceParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, true);
        this.options.put(RedmineReqSourceParser.OPTION_REQUIREMENT_DECL_CUSTOM_FIELD, "someValue");
        thrown.expect(ReqSourceParserException.class);
        thrown.expectMessage("You cannot set 'redmine.req.tag.required' to 'true' and use 'redmine.req.declaration.customfield' at the same time");
        thrown.expectCause(Is.isA(IllegalStateException.class));
        this.redmineReqSourceParser.parse(stubUri, tagConfig, options);
    }

	private void initOptions() {
		this.options = new HashMap<>();
		// this.options.put(RedmineReqSourceParser.OPTION_AUTH_API_KEY, "7ac3db9bcf094e273dc2c6ec34ddf2c56862f760"); // redmine.org need no auth
		this.options.put(RedmineReqSourceParser.OPTION_PROJECT_KEY, "1"); // or 'redmine' for http://redmine.org for example
		this.options.put(RedmineReqSourceParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, false);
		this.options.put(RedmineReqSourceParser.OPTION_TRACKER_FILTER, "3"); // Patch tracker
		this.options.put(RedmineReqSourceParser.OPTION_STATUS_FILTER, "open"); // *, open, closed, or tracker id
		this.options.put(RedmineReqSourceParser.OPTION_TARGET_VERSIONS, Arrays.asList(new String[] {}));
		this.options.put(RedmineReqSourceParser.OPTION_INCLUDE_CHILDREN, true);
		this.options.put(RedmineReqSourceParser.OPTION_INCLUDE_RELATIONS, true);
		final Properties extraProperties = new Properties();
		extraProperties.put("assigned_to_id", "1");
		this.options.put(RedmineReqSourceParser.OPTION_EXTRA_PROPERTIES, extraProperties);
	}

}
