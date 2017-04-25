package net.paissad.tools.reqcoco.generator.simple.api;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqSourceParserException;

@FunctionalInterface
public interface ReqSourceParser {

	/**
	 * @param uri - The {@link URI} containing the declarations of the requirements to parse.
	 * @param options - The options to use, if necessary, for parsing the source. Can be <code>null</code>.
	 * @return The list of {@link Requirement} retrieved after the parsing.
	 * @throws ReqSourceParserException If an error occurs while parsing the source of requirements.
	 */
	Collection<Requirement> parse(final URI uri, final Map<String, Object> options) throws ReqSourceParserException;
}
