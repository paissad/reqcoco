package net.paissad.tools.reqcoco.core.parser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.api.exception.ReqParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.api.model.Version;
import net.paissad.tools.reqcoco.api.parser.ReqSourceParser;

public abstract class AbstractReqSourceParser implements ReqSourceParser {

	private static final Logger	LOGGER				= LoggerFactory.getLogger(AbstractReqSourceParser.class);

	@Getter(value = AccessLevel.PRIVATE)
	@Setter(value = AccessLevel.PRIVATE)
	private boolean				sourceAlreadyParsed	= false;

	@Getter(value = AccessLevel.PRIVATE)
	@Setter(value = AccessLevel.PRIVATE)
	private SOURCE_TYPE			sourceType;

	@Getter(value = AccessLevel.PRIVATE)
	@Setter(value = AccessLevel.PRIVATE)
	private Requirements		cachedRequirements;

	@Override
	public Requirements getRequirements() throws ReqParserException {

		try {

			if (!isSourceAlreadyParsed()) {

				setSource(getURI(), getOptions());

				final JAXBContext jaxbContext = JAXBContext.newInstance(Requirements.class);
				final Unmarshaller u = jaxbContext.createUnmarshaller();

				switch (getSourceType()) {
				case FILE:
					final File f = Paths.get(getURI()).toFile();
					this.setCachedRequirements((Requirements) u.unmarshal(f));
					break;

				case URL:
					final URL url = getURI().toURL();
					this.setCachedRequirements((Requirements) u.unmarshal(url));
					break;

				default:
					break;
				}

				this.sanitizeRequirements();
			}

			return this.getCachedRequirements();

		} catch (Exception e) {
			String errMsg = "Error while retrieving requirements from the source : " + e.getMessage();
			LOGGER.error(errMsg, e);
			throw new ReqParserException(errMsg, e);
		}
	}

	@Override
	public Collection<Requirement> getRequirements(final Version version) throws ReqParserException {
		return Requirements.getByVersion(getRequirements().getRequirements(), version.getValue());
	}

	@Override
	public void setSource(final URI uri, final Map<String, Object> options) throws IOException {

		if (uri == null) {
			throw new NullPointerException("The URI to parse should is null");

		} else {
			if (uri.getScheme().matches("(?i)^https{0,1}$")) {
				this.setSourceType(SOURCE_TYPE.URL);

			} else if (uri.getScheme().matches("(?i)^file$")) {
				this.setSourceType(SOURCE_TYPE.FILE);

			} else {
				throw new UnsupportedOperationException("Unable to parse source from the scheme type --> " + uri.getScheme());
			}

			this.setSourceAlreadyParsed(true);
		}
	}

	private void sanitizeRequirements() {
		getCachedRequirements().getRequirements().stream().forEach(req -> {
			if (req.getVersion() == null || req.getVersion().getValue() == null || req.getVersion().getValue().trim().isEmpty()) {
				req.setVersion(Version.UNKNOWN);
			}
		});
	}

	protected abstract URI getURI() throws URISyntaxException;

	protected abstract Map<String, Object> getOptions();

	private enum SOURCE_TYPE {
		FILE, URL;
	}

}
