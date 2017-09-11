package net.paissad.tools.reqcoco.parser.github;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqSourceParserException;

public class GithubReqSourceParser implements ReqSourceParser {

    private static final Logger LOGGER                                 = LoggerFactory.getLogger(GithubReqSourceParser.class);

    public static final String  OPTION_AUTH_USERNAME                   = "github.auth.username";

    public static final String  OPTION_AUTH_PASS                       = "github.auth.password";

    public static final String  OPTION_AUTH_API_KEY                    = "github.oauth.api.key";

    public static final String  OPTION_REPO_OWNER                      = "github.repo.owner";

    public static final String  OPTION_REPO_NAME                       = "github.repo.name";

    public static final String  OPTION_ISSUES_FILTER_DATA              = "github.issues.filter";

    public static final String  OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT = "github.req.tag.required";

    public static final boolean DEFAULT_VALUE_REQUIREMENT_TAG_PRESENCE = false;

    @Override
    public Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig declTagConfig, final Map<String, Object> options) throws ReqSourceParserException {

        if (options == null || options.isEmpty()) {
            throw new ReqSourceParserException("Non null and non empty options must be passed in order to parse a Github project", null);
        }

        try {

            final String repoOwner = (String) options.get(OPTION_REPO_OWNER);
            final String repoName = (String) options.get(OPTION_REPO_NAME);
            final String authUsername = (String) options.get(OPTION_AUTH_USERNAME);
            final String authPassword = (String) options.get(OPTION_AUTH_PASS);
            final String authApiKey = (String) options.get(OPTION_AUTH_API_KEY);

            final GitHubClient gitHubClient = new GitHubClient();
            gitHubClient.setBufferSize(8192);

            if (!StringUtils.isBlank(authApiKey)) {
                gitHubClient.setOAuth2Token(authApiKey);

            } else if (!StringUtils.isAnyBlank(authUsername, authPassword)) {
                gitHubClient.setCredentials(authUsername, authPassword);

            } else {
                LOGGER.warn("No authentication method is specified for GITHUB. Continuing without authentication informations ...");
            }

            LOGGER.debug("Remaining requests : {}", gitHubClient.getRemainingRequests());

            final RepositoryService repositoryService = new RepositoryService(gitHubClient);

            final Repository repository = repositoryService.getRepository(repoOwner, repoName);

            final IssueService issueService = new IssueService(gitHubClient);
            final Map<String, String> defaultFilterData = new HashMap<>();
            defaultFilterData.put("state", "all");

            @SuppressWarnings("unchecked")
            final Map<String, String> filterData = (Map<String, String>) options.getOrDefault(OPTION_ISSUES_FILTER_DATA, defaultFilterData);

            final List<Issue> issues = issueService.getIssues(repository, filterData);

            final Collection<Requirement> declaredRequirements = Collections.synchronizedList(new ArrayList<>());

            final boolean reqTagRequiredInTitle = (boolean) options.getOrDefault(OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, DEFAULT_VALUE_REQUIREMENT_TAG_PRESENCE);

            final Pattern reqTagPattern = Pattern.compile(declTagConfig.getCompleteRegex());
            final Predicate<Issue> issueMatchPredicate = new IssueMatchPredicate(reqTagRequiredInTitle, reqTagPattern);

            issues.stream().filter(issueMatchPredicate).forEach(issue -> {
                final Requirement req = buildRequirementFromIssue(issue);
                declaredRequirements.add(req);
            });

            return declaredRequirements;

        } catch (Exception e) {
            String errMsg = "Error while retrieving Github issues : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqSourceParserException(errMsg, e);
        }
    }

    private static class IssueMatchPredicate implements Predicate<Issue> {

        private final Pattern reqTagPattern;

        private final boolean reqTagRequired;

        public IssueMatchPredicate(final boolean reqTagRequired, final Pattern reqTagPattern) {
            this.reqTagRequired = reqTagRequired;
            this.reqTagPattern = reqTagPattern;
        }

        @Override
        public boolean test(final Issue issue) {
            return reqTagRequired ? this.reqTagPattern.matcher(issue.getTitle()).find() : true;
        }
    }

    private Requirement buildRequirementFromIssue(final Issue issue) {

        final Requirement req = new Requirement("Issue " + issue.getNumber(), null, null);
        req.setShortDescription(issue.getTitle());
        req.setFullDescription(issue.getBodyText());
        req.setLink(issue.getUrl());

        return req;
    }
}
