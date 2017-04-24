package net.paissad.tools.reqcoco.core.parser.simple;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

import lombok.Getter;
import net.paissad.tools.reqcoco.core.parser.AbstractReqSourceParser;

public class PathReqSourceParser extends AbstractReqSourceParser {

	@Getter
	private Path				path;

	@Getter
	private Map<String, Object>	options;

	public PathReqSourceParser(final Path path, final Map<String, Object> options) {
		this.path = path;
		this.options = options;
	}

	@Override
	protected URI getURI() throws URISyntaxException {
		return getPath().toUri();
	}

}
