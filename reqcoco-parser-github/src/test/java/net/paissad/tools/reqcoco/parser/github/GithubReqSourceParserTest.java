package net.paissad.tools.reqcoco.parser.github;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.egit.github.core.client.RequestException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqParserException;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;

public class GithubReqSourceParserTest {

    private GithubReqDeclParser githubReqSourceParser;

    private ReqDeclTagConfig      tagConfig;

    private Map<String, Object>   options;

    @Rule
    public ExpectedException      thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.githubReqSourceParser = new GithubReqDeclParser();
        this.tagConfig = new SimpleReqDeclTagConfig();
        this.initOptions();
    }

    @Test
    public void testParse() throws ReqParserException {
        GithubTestUtil.assumePublicApiReachable();
        final Collection<Requirement> reqs = this.githubReqSourceParser.parse(null, tagConfig, options);
        Assert.assertFalse(reqs.isEmpty());
    }

    @Test
    public void testParseWithRequiredTagInIssueTitles() throws ReqParserException {
        GithubTestUtil.assumePublicApiReachable();
        options.put(GithubReqDeclParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, true);
        final Collection<Requirement> reqs = this.githubReqSourceParser.parse(null, tagConfig, options);
        Assert.assertTrue(reqs.isEmpty());
    }

    @Test
    public void testParseWithNullOptions() throws ReqParserException {
        GithubTestUtil.assumePublicApiReachable();
        thrown.expect(ReqParserException.class);
        thrown.expectMessage("Non null and non empty options must be passed in order to parse a Github project");
        this.githubReqSourceParser.parse(null, tagConfig, null);
    }

    @Test
    public void testParseWithEmptyOptions() throws ReqParserException {
        GithubTestUtil.assumePublicApiReachable();
        thrown.expect(ReqParserException.class);
        thrown.expectMessage("Non null and non empty options must be passed in order to parse a Github project");
        this.githubReqSourceParser.parse(null, tagConfig, new HashMap<>());
    }

    @Test
    public void testParseWithBadUserPasswordCredentials() throws ReqParserException {
        GithubTestUtil.assumePublicApiReachable();
        this.options.remove(GithubReqDeclParser.OPTION_AUTH_API_KEY);
        this.options.put(GithubReqDeclParser.OPTION_AUTH_USERNAME, "xxx");
        this.options.put(GithubReqDeclParser.OPTION_AUTH_PASS, UUID.randomUUID().toString());
        thrown.expect(ReqParserException.class);
        thrown.expectMessage("Error while retrieving Github issues : ");
        thrown.expectCause(Is.isA(RequestException.class));
        this.githubReqSourceParser.parse(null, tagConfig, options);
    }

    @Test
    public void testParseWithoutAuthentication() throws ReqParserException {
        GithubTestUtil.assumePublicApiReachable();
        this.options.remove(GithubReqDeclParser.OPTION_AUTH_API_KEY);
        try {
            final Collection<Requirement> reqs = this.githubReqSourceParser.parse(null, tagConfig, options);
            Assert.assertFalse(reqs.isEmpty());
        } catch (ReqParserException e) {
            if (e.getCause() != null && RequestException.class.equals(e.getCause().getClass()) && e.getCause().getMessage().contains("API rate limit exceeded")) {
                // Do nothing ... if the API rate limit is exceeded, we consider the test is ok !
            } else {
                throw e;
            }
        }
    }

    private void initOptions() {
        this.options = new HashMap<>();
        this.options.put(GithubReqDeclParser.OPTION_AUTH_API_KEY,
                new String(Base64.getDecoder().decode("YjVhNGE5MDA5YjViZThmMjRhNmYwYjVmM2RjYmIyMzQxM2M4ZWI1NQ=="), StandardCharsets.UTF_8));
        this.options.put(GithubReqDeclParser.OPTION_REPO_OWNER, "paissad");
        this.options.put(GithubReqDeclParser.OPTION_REPO_NAME, "reqcoco");
        this.options.put(GithubReqDeclParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, false);
    }
}
