package net.paissad.tools.reqcoco.parser.simple.spi;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqParserException;

/**
 * Represents a parser for retrieving all requirements declarations.
 * 
 * @author paissad
 */
public interface ReqDeclParser {

    /**
     * Represents all files extensions. This is supposed to be used when the parser is made for parsing local files without knowing the file extensions.
     */
    String ALL_EXTENSIONS = "__ALL_EXTENSIONS__";

    /**
     * @return The identifier for the parser. It must be unique among all registered/available parsers into the classpath.
     */
    String getIdentitier();

    /**
     * @param uri - The {@link URI} containing the declarations of the requirements to parse.
     * @param declTagConfig - The configuration which tells the parser how to parse the source by using the analyzing the tags.
     * @param options - The options to use, if necessary, for parsing the source. Can be <code>null</code>.
     * @return The list of {@link Requirement} retrieved after the parsing.
     * @throws ReqParserException If an error occurs while parsing the source of requirements.
     */
    Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig declTagConfig, final Map<String, Object> options) throws ReqParserException;

    /**
     * @return The files extensions that are supported for the parser.<br>
     *         Any extension is case insensitive.<br>
     *         This result can be <code>null</code> or empty.
     */
    Collection<String> getRegisteredFileExtensions();
}
