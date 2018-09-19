package net.paissad.tools.reqcoco.parser.simple.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqCodeTag;
import net.paissad.tools.reqcoco.parser.simple.api.ReqCodeTagConfig;
import net.paissad.tools.reqcoco.parser.simple.api.ReqGenerator;
import net.paissad.tools.reqcoco.parser.simple.api.ReqGeneratorConfig;
import net.paissad.tools.reqcoco.parser.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqGeneratorConfigException;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqGeneratorExecutionException;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.parser.simple.util.ReqGeneratorUtil;
import net.paissad.tools.reqcoco.parser.simple.util.ReqTagUtil;

public abstract class AbstractReqGenerator implements ReqGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReqGenerator.class);

    @Getter(value = AccessLevel.PROTECTED)
    private ReqGeneratorConfig  config;

    @Override
    public void configure(ReqGeneratorConfig cfg) throws ReqGeneratorConfigException {
        this.config = cfg;
    }

    @Override
    public Collection<Requirement> run() throws ReqGeneratorExecutionException {

        try {

            final URI sourceOfDeclaredReqs = getConfig().getSourceRequirements();

            LOGGER.info("Running the requirements coverage generator ...");
            LOGGER.info("The source for declared requirements is --> {}", sourceOfDeclaredReqs);

            final ReqSourceParser sourceParser = getConfig().getSourceParser();

            LOGGER.info("Retrieving declared requirements by parsing the source {}", sourceOfDeclaredReqs);
            final Collection<Requirement> declaredRequirements = Collections
                    .synchronizedCollection(sourceParser.parse(sourceOfDeclaredReqs, getConfig().getDeclTagConfig(), getConfig().getExtraOptions()));

            LOGGER.info("Tagging the requirements to ignore for the code and test coverage");
            final Collection<String> ignoreList = getConfig().getIgnoreList();
            if (!ignoreList.isEmpty()) {
                declaredRequirements.parallelStream().forEach(req -> {
                    if (ignoreList.contains(req.getName())) {
                        req.setIgnore(true);
                    }
                });
            }

            LOGGER.info("Parsing the source code in order to compute the source code coverage");
            final Path sourceCodePath = config.getSourceCodePath();

            if (!sourceCodePath.toFile().exists()) {
                String errMsg = "The path to lookup for source code coverage does not exist : " + sourceCodePath;
                LOGGER.error(errMsg);
                throw new ReqGeneratorExecutionException(errMsg, null);
            }
            parseCodeAndUpdateRequirements(declaredRequirements, sourceCodePath, CODE_TYPE.SOURCE);

            LOGGER.info("Parsing the tests code in order to compute the tests code coverage");
            final Path testsCodePath = config.getTestsCodePath();

            if (!testsCodePath.toFile().exists()) {
                String errMsg = "The path to lookup for tests code coverage does not exist : " + testsCodePath;
                LOGGER.error(errMsg);
                throw new ReqGeneratorExecutionException(errMsg, null);
            }
            parseCodeAndUpdateRequirements(declaredRequirements, testsCodePath, CODE_TYPE.TEST);

            final Path coverageOutputPath = getConfig().getCoverageOutput();
            if (coverageOutputPath != null) {
                LOGGER.info("Generating the coverage report to --> {}", coverageOutputPath);
                ReqGeneratorUtil.generateXmlCoverageReport(declaredRequirements, coverageOutputPath);
                LOGGER.info("The raw coverage report output file is --> {}", coverageOutputPath);
            }

            LOGGER.info("Finished executing the generator.");
            return declaredRequirements;

        } catch (ReqSourceParserException | IOException e) {
            String errMsg = "Error while parsing the source of declared requirements";
            LOGGER.error(errMsg, e);
            throw new ReqGeneratorExecutionException(errMsg, e);

        } catch (JAXBException e) {
            String errMsg = "Error while marshalling the output coverage report : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqGeneratorExecutionException(errMsg, e);
        }
    }

    private void parseCodeAndUpdateRequirements(final Collection<Requirement> requirements, final Path path, final CODE_TYPE codeType) throws IOException {

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

                if (mustParseFile(file)) {

                    LOGGER.trace("Parsing file {}", file);
                    final Collection<ReqCodeTag> tags = getTagsFromFile(file, codeType);
                    requirements.parallelStream().forEach(req -> tags.stream().forEach(tag -> {
                        if (isRequirementMatchTag(req, tag)) {
                            updateRequirementFromTag(req, tag, codeType);
                        }
                    }));
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                if (path.equals(dir)) {
                    return FileVisitResult.CONTINUE;
                } else {
                    return mustParseFile(dir) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
                }
            }
        });
    }

    /**
	 * @param file - The file to parse.
	 * @param codeType
	 * @return The list of the tags retrieved from the file passed in argument.
	 * @throws IOException
	 */
	private Collection<ReqCodeTag> getTagsFromFile(final Path file, final CODE_TYPE codeType) throws IOException {

		final Collection<ReqCodeTag> reqTags = new LinkedList<>();

		ReqCodeTagConfig tagConfigTmp = null;

		switch (codeType) {
		case SOURCE:
			tagConfigTmp = getConfig().getSourceCodeTagConfig();
			break;

		case TEST:
			tagConfigTmp = getConfig().getTestsCodeTagConfig();
			break;

		default:
			throw new UnsupportedOperationException("Unable to parse code for the type : " + codeType);
		}

		final ReqCodeTagConfig tagConfig = tagConfigTmp; // hack in order to have a final variable
		final Pattern patternTag = Pattern.compile(tagConfig.getCompleteRegex());

		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

			reader.lines().forEach(line -> {
			    line = ReqTagUtil.unEscapeString(line);
				final Matcher matcherTag = patternTag.matcher(line);
				while (matcherTag.find()) {
					final String tagAsString = matcherTag.group();
					reqTags.add(buildTagFromCodeDeclaration(tagConfig, tagAsString));
				}
			});
		} catch (Exception e) {
		    LOGGER.warn("Unable to retrieve tags from the following file --> {}", file);
		    LOGGER.trace("Reason : {}", e.getMessage());
		}

		return reqTags;
	}

    private ReqCodeTag buildTagFromCodeDeclaration(final ReqCodeTagConfig tagConfig, final String tagAsString) {

        // Retrieve the 'id' part of the tag
        final String id = ReqTagUtil.extractFieldValue(tagAsString, tagConfig.getIdRegex(), 1);
        if (id == null) {
            LOGGER.error("No id defined for requirement tag --> {}", tagAsString);
        }

        // Retrieve the 'version' part of the tag
        String version = ReqTagUtil.extractFieldValue(tagAsString, tagConfig.getVersionRegex(), 1);
        if (version == null) {
            LOGGER.warn("No version defined for tag --> {} <--- Version is going to be set to '{}'", tagAsString, Requirement.VERSION_UNKNOWN);
            version = Requirement.VERSION_UNKNOWN;
        }

        // Retrieve the 'revision' part of the tag
        final String revision = ReqTagUtil.extractFieldValue(tagAsString, tagConfig.getRevisionRegex(), 1);

        // Retrieve the 'author' part of the tag
        final String author = ReqTagUtil.extractFieldValue(tagAsString, tagConfig.getAuthorRegex(), 1);

        // Retrieve the 'comment' part of the tag
        final String comment = ReqTagUtil.extractFieldValue(tagAsString, tagConfig.getCommentRegex(), 1);

        // Build the req tag object
        final ReqCodeTag reqTag = new ReqCodeTag();
        reqTag.setId(id);
        reqTag.setVersion(version);
        reqTag.setRevision(revision);
        reqTag.setAuthor(author);
        reqTag.setComment(comment);

        return reqTag;
    }

    /**
     * Update the requirement passed in argument from the informations contained into the tag.
     * 
     * @param requirement - The requiremet to update.
     * @param tag - The tag
     * @param codeType
     */
    private void updateRequirementFromTag(final Requirement requirement, final ReqCodeTag tag, CODE_TYPE codeType) {

        switch (codeType) {
        case SOURCE:
            requirement.setCodeDone(true);
            requirement.setCodeAuthor(tag.getAuthor());
            requirement.setCodeAuthorComment(tag.getComment());
            break;

        case TEST:
            requirement.setTestDone(true);
            requirement.setTestAuthor(tag.getAuthor());
            requirement.setTestAuthorComment(tag.getComment());
            break;

        default:
            break;
        }
    }

    /**
     * @param requirement - The declared requirement from the parsed source.
     * @param tag - A tag from the code (source or tests)
     * @return <code>true</code> if the (id, version and revision) matched betweend the requirement and the tag.
     */
    private boolean isRequirementMatchTag(final Requirement requirement, final ReqCodeTag tag) {

        if (!StringUtils.isBlank(requirement.getName())) {
            boolean match = requirement.getName().equals(tag.getId()) && requirement.getVersion().equals(tag.getVersion());
            if (match && !StringUtils.isBlank(requirement.getRevision())) {
                final String revisionIntoCode = tag.getRevision();
                match = requirement.getRevision().equals(revisionIntoCode);
                if (!match && !StringUtils.isBlank(revisionIntoCode)) {
                    LOGGER.warn("The requirement's revision into the code is not up to date compared to what is declared into the source. The tag is -> {}", tag);
                } else if (StringUtils.isBlank(revisionIntoCode)) {
                    LOGGER.warn("A requirement with revision exists, but no revision is specified into the code tag -> {}", tag);
                }
            }
            return match;
        } else {
            return false; // id is blank from the source !
        }
    }

    /**
     * @param file - A file.
     * @return <code>true</code> if the file is included and not excluded.
     */
    private boolean mustParseFile(final Path file) {

        for (final String include : getConfig().getFileIncludes()) {
            final String includeRegex = buildIncludeOrExcludeRegex(include);
            if (file.getFileName().toString().matches(includeRegex)) {
                return !isFileExcluded(file);
            }
        }
        return false;
    }

    private boolean isFileExcluded(final Path path) {

        for (final String exclude : getConfig().getFileExcludes()) {
            final String excludeRegex = buildIncludeOrExcludeRegex(exclude);
            if (path.getFileName().toString().matches(excludeRegex)) {
                return true;
            }
        }
        return false;
    }

    private String buildIncludeOrExcludeRegex(final String str) {
        return new StringBuilder().append("^").append(str.replace("*", ".*")).append("$").toString();
    }

    private enum CODE_TYPE {
        SOURCE, TEST;
    }

}
