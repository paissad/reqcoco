package net.paissad.tools.reqcoco.parser.aggregator;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqParserException;
import net.paissad.tools.reqcoco.parser.simple.impl.AbstractReqDeclParser;
import net.paissad.tools.reqcoco.parser.simple.spi.ReqDeclParser;
import net.paissad.tools.reqcoco.parser.simple.spi.ReqDeclParserProvider;

/**
 * This implementation is not a real parser itself. It rather relies on several other parsers which will be used depending on the files extensions.
 * 
 * @author paissad
 */
public class AggregatorReqDeclParser extends AbstractReqDeclParser {

    private static final Logger LOGGER            = LoggerFactory.getLogger(AggregatorReqDeclParser.class);

    public static final String  PARSER_IDENTIFIER = "AGGREGATOR";

    @Override
    public String getIdentitier() {
        return PARSER_IDENTIFIER;
    }

    @Override
    public Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig declTagConfig, final Map<String, Object> options) throws ReqParserException {

        final Path resourcePath = Paths.get(uri);

        try {

            final Set<Requirement> declaredRequirements = new HashSet<>();

            Files.walkFileTree(resourcePath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    final String fileExtension = getFileExtension(file.getFileName().toString());
                    final ReqDeclParser reqDeclParser = ReqDeclParserProvider.getInstance().getParserForFileExtension(fileExtension);

                    try {
                        declaredRequirements.addAll(reqDeclParser.parse(file.toUri(), declTagConfig, options));

                    } catch (ReqParserException e) {
                        LOGGER.error("Error while parsing the file -->{}<-- by using the parser type --> {}", file, reqDeclParser.getClass());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

            return declaredRequirements;

        } catch (IOException e) {
            String errMsg = "Error while retrieving requirements declarations : " + e.getMessage();
            LOGGER.error(errMsg, e);
            throw new ReqParserException(errMsg, e);
        }
    }

    @Override
    public Collection<String> getRegisteredFileExtensions() {
        return Collections.emptyList();
    }

    private static String getFileExtension(final String filename) {
        return filename.replaceAll(".*(\\.\\p{Alnum}+)", "$1");
    }

}
