package net.paissad.tools.reqcoco.generator.simple.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.generator.simple.api.ReqGenerator;
import net.paissad.tools.reqcoco.generator.simple.api.ReqGeneratorConfig;
import net.paissad.tools.reqcoco.generator.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.api.TagConfig;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorConfigException;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqGeneratorExecutionException;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqSourceParserException;

public abstract class AbstractReqGenerator implements ReqGenerator {

	private static final Logger	LOGGER	= LoggerFactory.getLogger(AbstractReqGenerator.class);

	@Getter(value = AccessLevel.PROTECTED)
	private ReqGeneratorConfig	config;

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
			parseCodeAndUpdateRequirements(declaredRequirements, sourceCodePath, SOURCE_CODE_TYPE.CODE);

			LOGGER.info("Parsing the tests code in order to compute the tests coverage");
			final Path testsCodePath = config.getTestsCodePath();

			if (!testsCodePath.toFile().exists()) {
				String errMsg = "The path to lookup for tests coverage does not exist : " + testsCodePath;
				LOGGER.error(errMsg);
				throw new ReqGeneratorExecutionException(errMsg, null);
			}
			parseCodeAndUpdateRequirements(declaredRequirements, testsCodePath, SOURCE_CODE_TYPE.TESTS);

			final Path coverageOutputPath = getConfig().getCoverageOutput();
			LOGGER.info("Generating the coverage report to --> {}" + coverageOutputPath);

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

	private void parseCodeAndUpdateRequirements(final Collection<Requirement> requirements, final Path path, final SOURCE_CODE_TYPE type)
	        throws IOException {

		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				// TODO : code the tag parsing ...
				TagConfig tagConfig = null;

				switch (type) {
				case CODE:
					tagConfig = getConfig().getCodeTagConfig();
					break;

				case TESTS:
					tagConfig = getConfig().getTestsTagConfig();
					break;

				default:
					break;
				}

				if (mustParseFile(path)) {
					
					LOGGER.trace("Parsing file {}", path);
					// TODO: parse the file and check for requirements ...
				}

				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * @param path - A file.
	 * @return <code>true</code> if the file is included and not excluded.
	 */
	private boolean mustParseFile(final Path path) {

		for (final String include : getConfig().getFileIncludes()) {
			final String includeRegex = buildIncludeOrExcludeRegex(include);
			if (path.getFileName().toString().matches(includeRegex)) {
				return !isFileExcluded(path);
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

	private enum SOURCE_CODE_TYPE {
		CODE, TESTS;
	}

}
