package net.paissad.tools.reqcoco.parser.simple.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqParserException;

/**
 * This parser is intended to be used for simple text files or directories which contains only simple text files which contain the requirements declarations.
 * 
 * @author paissad
 */
public class FileReqDeclParser extends AbstractReqDeclParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReqDeclParser.class);

    /**
     * Can be used when the {@link URI} is a link to a file.
     */
    @Override
    public Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig declTagConfig, final Map<String, Object> options) throws ReqParserException {

        try {

            final Path resourcePath = Paths.get(uri);

            final Set<Requirement> declaredRequirements = new HashSet<>();

            Files.walkFileTree(resourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

                    try {

                        LOGGER.info("Parsing file for retrieving requirements declarations : {}", file);
                        declaredRequirements.addAll(parseSingleFile(declTagConfig, file));

                    } catch (Exception e) {
                        LOGGER.error("Unable to retrieve requirements declarations from the file --> {}", file);
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(final Path file, IOException exc) throws IOException {

                    // Is root resource ?
                    if (file.equals(resourcePath)) {

                        throw exc;

                    } else {

                        LOGGER.error("Technical error occured while visiting resource --> {}", file);
                        return FileVisitResult.CONTINUE;
                    }
                }
            });

            return declaredRequirements;

        } catch (Exception e) {
            String errMsg = "I/O error while parsing the source for retrieving the declared requirements : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqParserException(errMsg, e);
        }
    }

    private Collection<Requirement> parseSingleFile(final ReqDeclTagConfig declTagConfig, final Path resourcePath) throws IOException {

        try (final Stream<String> lines = Files.lines(resourcePath, StandardCharsets.UTF_8)) {

            final Set<Requirement> declaredRequirements = new HashSet<>();

            final Predicate<String> lineContainsRequirementPredicate = Pattern.compile(declTagConfig.getCompleteRegex()).asPredicate();

            lines.parallel().filter(lineContainsRequirementPredicate).forEach(line -> declaredRequirements.addAll(getRequirementsFromString(declTagConfig, line)));

            return declaredRequirements;
        }
    }

}
