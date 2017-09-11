package net.paissad.tools.reqcoco.runner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.core.report.AbstractReqReportBuilder;
import net.paissad.tools.reqcoco.parser.github.GithubReqSourceParser;
import net.paissad.tools.reqcoco.parser.redmine.RedmineReqSourceParser;

@Getter
@Setter
public class ReqRunnerOptions {

    private static final Logger LOGGER                        = LoggerFactory.getLogger(ReqRunnerOptions.class);

    public static final String  CONFIG_LOG_LEVEL              = "log.level";

    public static final String  CONFIG_SOURCE_CODE_PATH       = "code.source.path";

    public static final String  CONFIG_TEST_CODE_PATH         = "code.test.path";

    public static final String  CONFIG_RESOURCE_INCLUDES      = "resource.includes";

    public static final String  CONFIG_RESOURCE_EXCLUDES      = "resource.excludes";

    public static final String  CONFIG_REQUIREMENT_IGNORES    = "requirement.ignores";

    public static final String  CONFIG_REPORT_CONSOLE         = "report.console";

    public static final String  CONFIG_REPORT_HTML            = "report.html";

    public static final String  CONFIG_REPORT_EXCEL           = "report.excel";

    public static final String  CONFIG_REPORT_ZIP             = "report.zip";

    public static final String  CONFIG_CREATE_RAW_REPORT_FILE = "create.raw.report.file";

    private String              logLevel;

    private String              sourceCodePath;

    private String              testCodePath;

    private String              resourceIncludes;

    private String              resourceExcludes;

    private String              ignores;

    private boolean             reportConsole;

    private boolean             reportHtml;

    private boolean             reportExcel;

    private boolean             reportZip;

    private boolean             createRawReportFile;

    private Properties          configProperties;

    @Option(name = "-h", aliases = { "--help" }, help = true, usage = "Shows the help.")
    private boolean             help;

    @Option(name = "--config", required = true, usage = "The configuration file.")
    private File                configFile;

    @Option(name = "--input-type", required = true, usage = "The type of the requirements source declaration.")
    private ReqSourceType       sourceType;

    @Option(name = "--input", required = true, metaVar = "<input>", usage = "Source containing the requirements to parse.")
    private String              requirementSource;

    @Option(name = "--output", required = true, metaVar = "<dir>", usage = "Directory where to store the coverage reports.")
    private String              outputFolder;

    @Option(name = "--report-name", required = false, metaVar = "[name]", usage = "The name of the report file. The default value is '"
            + AbstractReqReportBuilder.DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION + "'")
    private String              reportName;

    @Argument
    private List<String>        arguments                     = new ArrayList<>();

    /**
     * @param args : arguments / options.
     * @return The command line parser.
     * @throws CmdLineException If an error occurs while parsing the options.
     */
    public CmdLineParser parseOptions(final String... args) throws CmdLineException {

        final CmdLineParser parser = new CmdLineParser(this);

        ParserProperties.defaults().withUsageWidth(150);

        try {
            parser.parseArgument(args);

        } catch (CmdLineException e) {
            System.err.println();
            System.err.println("======> " + e.getMessage());
            System.err.println();
            printUsage(parser);
            throw e;
        }

        return parser;
    }

    public void parseConfigFile() throws IOException {

        try (final InputStream in = Files.newInputStream(getConfigFile().toPath())) {

            this.setConfigProperties(new Properties());
            this.getConfigProperties().load(in);
            this.buildOptionsFromProperties(getConfigProperties());
            this.updatePropertiesForRedmine(getConfigProperties());
            this.updatePropertiesForGithub(getConfigProperties());

        } catch (NullPointerException | IOException e) {
            String errMsg = "Error while parsing the configuration file : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new IOException(errMsg, e);
        }
    }

    private void buildOptionsFromProperties(final Properties props) {

        this.setLogLevel(props.getProperty(CONFIG_LOG_LEVEL, "INFO"));
        this.setSourceCodePath(props.getProperty(CONFIG_SOURCE_CODE_PATH));
        this.setTestCodePath(props.getProperty(CONFIG_TEST_CODE_PATH));
        this.setResourceIncludes(props.getProperty(CONFIG_RESOURCE_INCLUDES, "*"));
        this.setResourceExcludes(props.getProperty(CONFIG_RESOURCE_EXCLUDES, ""));
        this.setIgnores(props.getProperty(CONFIG_REQUIREMENT_IGNORES, ""));
        this.setReportConsole(Boolean.parseBoolean(props.getProperty(CONFIG_REPORT_CONSOLE, Boolean.FALSE.toString())));
        this.setReportHtml(Boolean.parseBoolean(props.getProperty(CONFIG_REPORT_HTML, Boolean.TRUE.toString())));
        this.setReportExcel(Boolean.parseBoolean(props.getProperty(CONFIG_REPORT_EXCEL, Boolean.TRUE.toString())));
        this.setReportZip(Boolean.parseBoolean(props.getProperty(CONFIG_REPORT_ZIP, Boolean.TRUE.toString())));
        this.setCreateRawReportFile(Boolean.parseBoolean(props.getProperty(CONFIG_CREATE_RAW_REPORT_FILE, Boolean.FALSE.toString())));
    }

    /**
     * This method update the properties loaded from the configuration file by setting the correct value type (Boolean, Collection ...) in order to avoid ClassCastException
     * exception.
     * 
     * @param props
     */
    private void updatePropertiesForRedmine(final Properties props) {

        final String statusFilter = StringUtils.isBlank(props.getProperty(RedmineReqSourceParser.OPTION_STATUS_FILTER)) ? RedmineReqSourceParser.DEFAULT_VALUE_STATUS_FILTER
                : props.getProperty(RedmineReqSourceParser.OPTION_STATUS_FILTER);
        props.put(RedmineReqSourceParser.OPTION_STATUS_FILTER, statusFilter);

        final boolean includeChildren = StringUtils.isBlank(props.getProperty(RedmineReqSourceParser.OPTION_INCLUDE_CHILDREN))
                ? RedmineReqSourceParser.DEFAULT_VALUE_INCLUDE_CHILDREN
                : Boolean.parseBoolean(props.getProperty(RedmineReqSourceParser.OPTION_INCLUDE_CHILDREN));
        props.put(RedmineReqSourceParser.OPTION_INCLUDE_CHILDREN, includeChildren);

        final boolean includeRelations = StringUtils.isBlank(props.getProperty(RedmineReqSourceParser.OPTION_INCLUDE_RELATIONS))
                ? RedmineReqSourceParser.DEFAULT_VALUE_INCLUDE_RELATIONS
                : Boolean.parseBoolean(props.getProperty(RedmineReqSourceParser.OPTION_INCLUDE_RELATIONS));
        props.put(RedmineReqSourceParser.OPTION_INCLUDE_RELATIONS, includeRelations);

        final Collection<String> targetVersions = StringUtils.isBlank((String) props.get(RedmineReqSourceParser.OPTION_TARGET_VERSIONS))
                ? RedmineReqSourceParser.getDefautValueForTargetVersions()
                : Arrays.asList(props.get(RedmineReqSourceParser.OPTION_TARGET_VERSIONS).toString().split(","));
        props.put(RedmineReqSourceParser.OPTION_TARGET_VERSIONS, targetVersions);

        final boolean reqTagMustBePresent = StringUtils.isBlank(props.getProperty(RedmineReqSourceParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT))
                ? RedmineReqSourceParser.DEFAULT_VALUE_REQUIREMENT_TAG_PRESENCE
                : Boolean.parseBoolean(props.getProperty(RedmineReqSourceParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT));
        props.put(RedmineReqSourceParser.OPTION_REQUIREMENT_TAG_MUST_BE_PRESENT, reqTagMustBePresent);

        final Properties redmineExtraProperties = getRedmineExtraProperties();
        if (redmineExtraProperties != null) {
            props.put(RedmineReqSourceParser.OPTION_EXTRA_PROPERTIES, redmineExtraProperties);
        }
    }

    public Properties getRedmineExtraProperties() {
        return getComplexProperties(RedmineReqSourceParser.OPTION_EXTRA_PROPERTIES);
    }

    private void updatePropertiesForGithub(final Properties props) {
        final Properties githubIssuesFilters = getComplexProperties(GithubReqSourceParser.OPTION_ISSUES_FILTER_DATA);
        if (githubIssuesFilters != null) {
            props.put(GithubReqSourceParser.OPTION_ISSUES_FILTER_DATA,
                    githubIssuesFilters.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue())));
        }
    }

    private Properties getComplexProperties(final String prefix) {
        Properties complexProps = null;
        final Predicate<String> predicate = Pattern.compile(Pattern.quote(prefix + ".key.")).asPredicate();

        final Set<String> filterKeys = getConfigProperties().keySet().stream().map(Object::toString).filter(predicate).collect(Collectors.toSet());

        if (!filterKeys.isEmpty()) {
            complexProps = new Properties();
            for (final String key : filterKeys) {
                String suffix = key.substring((prefix + ".key.").length(), key.length());
                String extraPropsKey = getConfigProperties().getProperty(key);
                String extraPropsValue = getConfigProperties().getProperty(prefix + ".value." + suffix);
                complexProps.put(extraPropsKey, extraPropsValue);
            }
        }

        return complexProps;
    }

    public static Map<String, Object> mapFromProperties(final Properties properties) {
        return properties.keySet().stream().map(Object::toString).collect(Collectors.toMap(Function.identity(), properties::get));
    }

    /**
     * Prints the usage.
     * 
     * @param parser - The command line parser.
     */
    public static void printUsage(final CmdLineParser parser) {
        System.out.println("reqcoco-runner [options...] arguments...");
        parser.printUsage(System.out);
        System.out.println();

        System.out.println("  Example: reqcoco-runner " + parser.printExample(filter -> !filter.option.help()));
    }
}
