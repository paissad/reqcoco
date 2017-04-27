package net.paissad.tools.reqcoco.generator.simple.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.generator.simple.api.ReqCodeTag;
import net.paissad.tools.reqcoco.generator.simple.api.ReqGenerator;
import net.paissad.tools.reqcoco.generator.simple.api.ReqGeneratorConfig;
import net.paissad.tools.reqcoco.generator.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.api.ReqTagConfig;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorConfigException;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorExecutionException;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqSourceParserException;

public abstract class AbstractReqGenerator implements ReqGenerator {

	private static final Logger		LOGGER	= LoggerFactory.getLogger(AbstractReqGenerator.class);

	private static final Charset	UTF8	= Charset.forName("UTF-8");

	@Getter(value = AccessLevel.PROTECTED)
	private ReqGeneratorConfig		config;

	@Override
	public void configure(ReqGeneratorConfig cfg) throws ReqGeneratorConfigException {
		this.config = cfg;
	}

	@Override
	public void run() throws ReqGeneratorExecutionException {

		try {

			final URI sourceOfDeclaredReqs = getConfig().getSourceRequirements();

			LOGGER.info("Running the requirements coverage generator ...");
			LOGGER.info("The source for declared requirements is --> {}", sourceOfDeclaredReqs);

			final ReqSourceParser sourceParser = getConfig().getSourceParser();

			LOGGER.info("Retrieving declared requirements by parsing the source {}", sourceOfDeclaredReqs);
			final Collection<Requirement> declaredRequirements = sourceParser.parse(sourceOfDeclaredReqs, getConfig().getExtraOptions());

			LOGGER.info("Tagging the requirements to ignore for the code and test coverage");
			final Collection<String> ignoreList = getConfig().getIgnoreList();
			if (ignoreList != null && !ignoreList.isEmpty()) {
				declaredRequirements.parallelStream().forEach(req -> {
					if (ignoreList.contains(req.getId())) {
						req.setIgnore(true);
					}
				});
			}

			LOGGER.info("Parsing the source code in order to compute the code coverage");
			final Path sourceCodePath = config.getSourceCodePath();

			if (!sourceCodePath.toFile().exists()) {
				String errMsg = "The path to lookup for code coverage does not exist : " + sourceCodePath;
				LOGGER.error(errMsg);
				throw new ReqGeneratorExecutionException(errMsg, null);
			}
			parseCodeAndUpdateRequirements(declaredRequirements, sourceCodePath, CODE_TYPE.SOURCE);

			LOGGER.info("Parsing the tests code in order to compute the tests coverage");
			final Path testsCodePath = config.getTestsCodePath();

			if (!testsCodePath.toFile().exists()) {
				String errMsg = "The path to lookup for tests coverage does not exist : " + testsCodePath;
				LOGGER.error(errMsg);
				throw new ReqGeneratorExecutionException(errMsg, null);
			}
			parseCodeAndUpdateRequirements(declaredRequirements, testsCodePath, CODE_TYPE.TEST);

			final Path coverageOutputPath = getConfig().getCoverageOutput();
			LOGGER.info("Generating the coverage report to --> {}", coverageOutputPath);

			final Requirements rootReqs = new Requirements();
			rootReqs.setRequirements(declaredRequirements);

			final JAXBContext jaxbContext = JAXBContext.newInstance(Requirements.class);
			final Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(rootReqs, coverageOutputPath.toFile());

			LOGGER.info("Finished executing the generator. The coverage report output is --> {}", coverageOutputPath);

		} catch (ReqSourceParserException | IOException e) {
			String errMsg = "Error while parsing the source of declared requirements ";
			LOGGER.error(errMsg, e);
			throw new ReqGeneratorExecutionException(errMsg, e);

		} catch (JAXBException e) {
			String errMsg = "Error while marshalling the output coverage report : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqGeneratorExecutionException(errMsg, e);
		}
	}

	private void parseCodeAndUpdateRequirements(final Collection<Requirement> requirements, final Path path, final CODE_TYPE codeType)
	        throws IOException {

		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

				if (mustParseFile(path)) {

					LOGGER.trace("Parsing file {}", path);
					final Collection<ReqCodeTag> tags = getTagsFromFile(path, codeType);
					requirements.parallelStream().forEach(req -> tags.stream().forEach(tag -> {
						if (isRequirementMatchTag(req, tag)) {
							updateRequirementFromTag(req, tag, codeType);
						}
					}));
				}

				return FileVisitResult.CONTINUE;
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

		ReqTagConfig tagConfig = null;

		switch (codeType) {
		case SOURCE:
			tagConfig = getConfig().getSourceCodeTagConfig();
			break;

		case TEST:
			tagConfig = getConfig().getTestsCodeTagConfig();
			break;

		default:
			throw new UnsupportedOperationException("Unable to parse code for the type : " + codeType);
		}

		final Pattern patternTag = Pattern.compile(tagConfig.getCompleteRegex());
		final Pattern patternId = Pattern.compile(tagConfig.getIdRegex());
		final Pattern patternVersion = Pattern.compile(tagConfig.getVersionRegex());
		final Pattern patternRevision = Pattern.compile(tagConfig.getRevisionRegex());
		final Pattern patternAuthor = Pattern.compile(tagConfig.getAuthorRegex());
		final Pattern patternComment = Pattern.compile(tagConfig.getCommentRegex());

		try (BufferedReader reader = Files.newBufferedReader(file, UTF8)) {

			reader.lines().filter(patternTag.asPredicate()).forEach(line -> {
				// At this step, the line matched the patter tag predicate, we can start the retrieval of the tag(s) and parts of the tag(s)
				final Matcher matcherTag = patternTag.matcher(line);
				while (matcherTag.find()) {

					final String tag = matcherTag.group();

					// Retrieve the 'id' part of the tag
					String id = null;
					if (patternId.matcher(tag).find()) {
						id = patternId.matcher(tag).group(1);
					} else {
						LOGGER.error("No id defined for requirement tag --> {}", tag);
					}

					// Retrieve the 'version' part of the tag
					String version = null;
					if (patternVersion.matcher(tag).find()) {
						version = patternVersion.matcher(tag).group(1);
					} else {
						LOGGER.warn("No version defined for tag --> {} <--- Version is going to be set to '{}'", tag, Requirement.VERSION_UNKNOWN);
					}

					// Retrieve the 'revision' part of the tag
					String revision = null;
					if (patternRevision.matcher(tag).find()) {
						revision = patternRevision.matcher(tag).group(1);
					}

					// Retrieve the 'author' part of the tag
					String author = null;
					if (patternAuthor.matcher(tag).find()) {
						author = patternAuthor.matcher(tag).group(1);
					}

					// Retrieve the 'comment' part of the tag
					String comment = null;
					if (patternComment.matcher(tag).find()) {
						patternComment.matcher(tag).group(1);
					}

					// Build the req tag object
					final ReqCodeTag reqTag = new ReqCodeTag();
					reqTag.setId(id);
					reqTag.setVersion(version);
					reqTag.setRevision(revision);
					reqTag.setAuthor(author);
					reqTag.setComment(comment);

					reqTags.add(reqTag);
				}
			});

		} catch (IOException e) {
			String errMsg = "An error occured while parsing/retrieving requirement tags from the file --> " + file;
			LOGGER.error(errMsg, e);
			throw new IOException(errMsg, e);
		}

		return reqTags;
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

		return requirement.getId().equals(tag.getId()) && requirement.getVersion().equals(tag.getVersion())
		        && requirement.getRevision().equals(tag.getRevision());
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
