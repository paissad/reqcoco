package net.paissad.tools.reqcoco.parser.redmine;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Version;
import com.taskadapter.redmineapi.internal.Transport;
import com.taskadapter.redmineapi.internal.URIConfigurator;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.parser.simple.util.ReqTagUtil;

public class RedmineReqSourceParser implements ReqSourceParser {

    private static final Logger LOGGER                                   = LoggerFactory.getLogger(RedmineReqSourceParser.class);

    public static final String  OPTION_PROJECT_KEY                       = "redmine.project.id";

    public static final String  OPTION_INCLUDE_CHILDREN                  = "redmine.include.children";

    public static final String  OPTION_INCLUDE_RELATIONS                 = "redmine.include.relations";

    public static final String  OPTION_TARGET_VERSIONS                   = "redmine.target.versions";

    public static final String  OPTION_TRACKER_FILTER                    = "redmine.tracker.filter";

    public static final String  OPTION_STATUS_FILTER                     = "redmine.status.filter";

    public static final String  OPTION_AUTH_USER_NAME                    = "redmine.auth.username";

    public static final String  OPTION_AUTH_USER_PASS                    = "redmine.auth.password";

    public static final String  OPTION_AUTH_API_KEY                      = "redmine.auth.apikey";

    public static final String  OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT   = "redmine.req.tag.required";

    public static final String  OPTION_REQUIREMENT_DECL_CUSTOM_FIELD     = "redmine.req.declaration.customfield";

    public static final String  OPTION_REQUIREMENT_REVISION_CUSTOM_FIELD = "redmine.req.revision.customfield";

    public static final String  OPTION_EXTRA_PROPERTIES                  = "redmine.extra.properties";

    public static final boolean DEFAULT_VALUE_INCLUDE_CHILDREN           = true;

    public static final boolean DEFAULT_VALUE_INCLUDE_RELATIONS          = true;

    public static final String  DEFAULT_VALUE_STATUS_FILTER              = "*";

    public static final boolean DEFAULT_VALUE_REQUIREMENT_TAG_PRESENCE   = false;

    @Override
    public Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig tagConfig, final Map<String, Object> options) throws ReqSourceParserException {

        if (uri == null) {
            throw new ReqSourceParserException("The root URL of Redmine cannot be null !", null);
        }

        if (options == null || options.isEmpty()) {
            throw new ReqSourceParserException("Non null and non empty options must be passed in order to parse a Redmine project", null);
        }

        try {

            // Retrieve options
            final String projectKey = (String) options.get(OPTION_PROJECT_KEY);
            final boolean includeChildren = (Boolean) options.getOrDefault(OPTION_INCLUDE_CHILDREN, DEFAULT_VALUE_INCLUDE_CHILDREN);
            final boolean includeRelations = (Boolean) options.getOrDefault(OPTION_INCLUDE_RELATIONS, DEFAULT_VALUE_INCLUDE_RELATIONS);

            final String statusFilter = (String) options.getOrDefault(OPTION_STATUS_FILTER, DEFAULT_VALUE_STATUS_FILTER);
            final String trackerFilter = (String) options.get(OPTION_TRACKER_FILTER);

            @SuppressWarnings("unchecked")
            final Collection<String> targetVersions = (Collection<String>) options.getOrDefault(OPTION_TARGET_VERSIONS, getDefautValueForTargetVersions());

            final String authUsername = (String) options.get(OPTION_AUTH_USER_NAME);
            final String authPassword = (String) options.get(OPTION_AUTH_USER_PASS);
            final String authApiAccessKey = (String) options.get(OPTION_AUTH_API_KEY);

            final boolean reqTagMustBePresent = (Boolean) options.getOrDefault(OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, DEFAULT_VALUE_REQUIREMENT_TAG_PRESENCE);
            final String customFieldDecl = (String) options.get(OPTION_REQUIREMENT_DECL_CUSTOM_FIELD);
            final String customFieldRevision = (String) options.get(OPTION_REQUIREMENT_REVISION_CUSTOM_FIELD);

            final Properties extraProperties = (Properties) options.getOrDefault(OPTION_EXTRA_PROPERTIES, new Properties());

            // Check that all options meet minimum requirements
            if (StringUtils.isBlank(projectKey)) {
                throw new ReqSourceParserException("A non null project id or name must be provided for parsing requirements from Redmine", null);
            }

            LOGGER.info("Retrieving Redmine issues ...");

            final Transport transport = buildTransport(uri.toString(), authApiAccessKey, authUsername, authPassword);

            final List<String> includes = new ArrayList<>();
            if (includeChildren) {
                LOGGER.debug("Include Redmine children issues");
                includes.add("children");
            }
            if (includeRelations) {
                LOGGER.debug("Includes Redmine related issues");
                includes.add("relations");
            }

            LOGGER.debug("Building the API request parameters to pass to Redmine");
            final List<NameValuePair> parameters = new ArrayList<>();

            parameters.add(new BasicNameValuePair("project_id", projectKey));

            if (!StringUtils.isBlank(trackerFilter)) {
                parameters.add(new BasicNameValuePair("tracker_id", trackerFilter));
            }

            parameters.add(new BasicNameValuePair("status_id", statusFilter));

            if (!includes.isEmpty()) {
                parameters.add(new BasicNameValuePair("include", includes.stream().collect(Collectors.joining(","))));
            }

            if (!extraProperties.isEmpty()) {
                extraProperties.keySet().stream().map(Object::toString).forEach(key -> parameters.add(new BasicNameValuePair(key, extraProperties.getProperty(key))));
            }

            LOGGER.info("Making HTTP request to Redmine API");

            final List<Issue> issues = Collections.synchronizedList(transport.getObjectsList(Issue.class, parameters));

            final Collection<Requirement> declaredRequirements = Collections.synchronizedList(new ArrayList<>());

            final Predicate<Issue> reqIssuePredicate = new IssueMatchPredicate(tagConfig, reqTagMustBePresent, customFieldDecl, targetVersions);

            LOGGER.info("Building requirements objects from Redmine issues");
            issues.parallelStream().filter(reqIssuePredicate).forEach(issue -> {
                final Requirement req = buildRequirementFromIssue(uri, issue, reqTagMustBePresent, customFieldRevision, tagConfig);
                declaredRequirements.add(req);
            });

            LOGGER.info("{} requirements have been built from Redmine issues", declaredRequirements.size());
            return declaredRequirements;

        } catch (NullPointerException | ClassCastException e) {
            String errMsg = "Error either while retrieving options, or while processing the API result";
            LOGGER.error(errMsg, e);
            throw new ReqSourceParserException(errMsg, e);

        } catch (RedmineException e) {
            String errMsg = "Error while retrieving Redmine issues : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqSourceParserException(errMsg, e);

        } catch (Exception e) {
            String errMsg = "Unexpected error ==> " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqSourceParserException(errMsg, e);
        }
    }

    private Transport buildTransport(final String uri, String authApiAccessKey, String authUsername, String authPassword) {

        Transport transport;

        if (!StringUtils.isBlank(authApiAccessKey)) {
            LOGGER.info("Prepare to log into Redmine '{}' by using API access key method", uri);
            transport = new Transport(new URIConfigurator(uri, authApiAccessKey), RedmineManagerFactory.createDefaultHttpClient());

        } else if (!StringUtils.isBlank(authUsername)) {
            LOGGER.info("Prepare to log into Redmine '{}' by using username/password method", uri);
            transport = new Transport(new URIConfigurator(uri, null), RedmineManagerFactory.createDefaultHttpClient());
            transport.setCredentials(authUsername, authPassword);

        } else {
            LOGGER.info("Prepare to log into Redmine '{}' without authentication", uri);
            transport = new Transport(new URIConfigurator(uri, null), RedmineManagerFactory.createDefaultHttpClient());
            transport.setCredentials(null, null);
        }

        return transport;
    }

    private Requirement buildRequirementFromIssue(final URI rootUri, final Issue issue, final boolean issueSubjectMustContainTag, final String revisionFromCustomField,
            final ReqDeclTagConfig tagConfig) {

        final Version issueVersion = issue.getTargetVersion();
        final String reqVersion = (issueVersion != null) ? issueVersion.getName() : Requirement.VERSION_UNKNOWN;
        String revision = null;
        // The revision from the custom field takes precedence over the revision defined from the tag into the subject.
        if (!StringUtils.isBlank(revisionFromCustomField)) {
            revision = getRevisionFromCustomField(issue, revisionFromCustomField);
        }
        if (StringUtils.isBlank(revision)) {
            revision = issueSubjectMustContainTag ? getRevisionFromSubject(issue, tagConfig) : null;
        }
        final Requirement req = new Requirement(issue.getId().toString(), reqVersion, revision);
        req.setShortDescription(ReqTagUtil.stripTagAndTrim(issue.getSubject(), tagConfig));
        req.setFullDescription(issue.getDescription());
        req.setLink(rootUri.toString().replaceAll("/+$", "") + "/issues/" + issue.getId());
        return req;
    }

    private static String getRevisionFromSubject(final Issue issue, final ReqDeclTagConfig tagConfig) {
        return ReqTagUtil.extractFieldValue(issue.getSubject(), tagConfig.getRevisionRegex(), 1);
    }

    private static String getRevisionFromCustomField(final Issue issue, final String revisionCustomFieldName) {
        if (!StringUtils.isBlank(revisionCustomFieldName)) {
            final CustomField customField = issue.getCustomFieldByName(revisionCustomFieldName);
            return customField != null ? customField.getValue() : null;
        } else {
            return null;
        }
    }

    public static Set<String> getDefautValueForTargetVersions() {
        return new HashSet<>();
    }

    private static class IssueMatchPredicate implements Predicate<Issue> {

        private final Pattern      reqTagPattern;

        private final boolean      subjectMustContainTag;

        private final String       declarationCustomFieldName;

        private Collection<String> versionsToMatch;

        public IssueMatchPredicate(final ReqDeclTagConfig tagConfig, final boolean subjectMustContainTag, final String declarationCustomFieldName,
                final Collection<String> versionsToMatch) {
            this.reqTagPattern = Pattern.compile(tagConfig.getCompleteRegex());
            this.subjectMustContainTag = subjectMustContainTag;
            this.declarationCustomFieldName = declarationCustomFieldName;
            this.versionsToMatch = versionsToMatch;
        }

        @Override
        public boolean test(final Issue issue) {
            this.checkRules();
            return isRequirement(issue) && isVersionMatch(issue);
        }

        /**
         * @throws IllegalStateException If all expected rules are not respected.
         */
        private void checkRules() {
            if (this.subjectMustContainTag && !StringUtils.isBlank(this.declarationCustomFieldName)) {
                throw new IllegalStateException(
                        "You cannot set '" + OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT + "' to 'true' and use '" + OPTION_REQUIREMENT_DECL_CUSTOM_FIELD + "' at the same time");
            }
        }

        /**
         * @param issue - The issue to check.
         * @return <code>true</code> if the issue has to be considered as a requirement, <code>false</code> otherwise.
         */
        private boolean isRequirement(final Issue issue) {

            if (this.subjectMustContainTag) {
                return this.reqTagPattern.matcher(issue.getSubject()).find();

            } else if (!StringUtils.isBlank(this.declarationCustomFieldName)) {
                return isCustomFieldDeclarationTrue(issue);

            } else {
                return true; // All issues are considered as requirements
            }
        }

        /**
         * @param issue - The issue to check.
         * @return <code>true</code> if the issue has the specified custom field <strong>AND</strong> has the value <strong>true</strong>. The
         *         verification is case insensitive.
         */
        private boolean isCustomFieldDeclarationTrue(final Issue issue) {
            final CustomField customField = issue.getCustomFieldByName(this.declarationCustomFieldName);
            return customField != null && Boolean.parseBoolean(customField.getValue());
        }

        private boolean isVersionMatch(final Issue issue) {
            if (versionsToMatch.isEmpty()) {
                return true;
            } else {
                return issue.getTargetVersion() != null && this.versionsToMatch.contains(issue.getTargetVersion().getName());
            }
        }
    }

}
