package net.paissad.tools.reqcoco.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import net.paissad.tools.reqcoco.api.exception.ReqReportParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.parser.ReqReportParser;
import net.paissad.tools.reqcoco.core.parser.AbstractReqReportParser;

public class TestUtil {

	public static final URI	REQUIREMENTS_INPUT_FILE1_XML_URI;

	public static final URI	REQUIREMENTS_INPUT_FILE2_XML_URI;

	public static final URI	REQUIREMENTS_INPUT_FILE_EMPTY_XML_URI;

	public static final URI	REQUIREMENTS_INTPUT_MALFORMED_SOURCE1_XML_URI;

	static {
		try {

			REQUIREMENTS_INPUT_FILE1_XML_URI = TestUtil.class.getResource("/requirements_input/file1.xml").toURI();
			REQUIREMENTS_INPUT_FILE2_XML_URI = TestUtil.class.getResource("/requirements_input/file2.xml").toURI();
			REQUIREMENTS_INPUT_FILE_EMPTY_XML_URI = TestUtil.class.getResource("/requirements_input/empty.xml").toURI();
			REQUIREMENTS_INTPUT_MALFORMED_SOURCE1_XML_URI = TestUtil.class.getResource("/requirements_input/malformed_source1.xml").toURI();

		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	private TestUtil() {
	}

	public static Collection<Requirement> getRequirementsFromStub(final URI uri, final Map<String, Object> options) throws ReqReportParserException {
		final ReqReportParser parser = initAbstractRequirementSourceParser(uri, options);
		return parser.getRequirements().getRequirements();
	}

	public static ReqReportParser initAbstractRequirementSourceParser(final URI uri, final Map<String, Object> options) {
		return new AbstractReqReportParser() {

			@Override
			protected URI getURI() throws URISyntaxException {
				return uri;
			}

			@Override
			protected Map<String, Object> getOptions() {
				return options;
			}
		};
	}
}
