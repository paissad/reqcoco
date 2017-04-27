package net.paissad.tools.reqcoco.core.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.xml.bind.UnmarshalException;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.api.exception.ReqReportParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.api.parser.ReqReportParser;
import net.paissad.tools.reqcoco.core.TestUtil;

public class AbstractReqReportParserTest {

	private ReqReportParser		requirementSourceParser;

	@Rule
	public ExpectedException	thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.setUpByUsingUri(TestUtil.REQUIREMENTS_INPUT_FILE1_XML_URI);
	}

	@Test
	public void testGetRequirements() throws ReqReportParserException {
		final Requirements reqs = requirementSourceParser.getRequirements();
		Assert.assertNotNull(reqs);
		Assert.assertEquals(5, reqs.getRequirements().stream().count());
	}

	/**
	 * The XML source does not have a well formatted content.
	 * 
	 * @throws ReqReportParserException
	 */
	@Test
	public void testGetRequirementsBadContent() throws ReqReportParserException {

		thrown.expect(ReqReportParserException.class);
		thrown.expectCause(Is.isA(UnmarshalException.class));
		thrown.expectMessage("Error while retrieving requirements from the source : ");

		this.setUpByUsingUri(TestUtil.REQUIREMENTS_INTPUT_MALFORMED_SOURCE1_XML_URI);
		this.requirementSourceParser.getRequirements();
	}

	@Test
	public void testGetRequirementsNotSupportedUriScheme() throws ReqReportParserException, URISyntaxException {
		thrown.expectCause(Is.isA(UnsupportedOperationException.class));
		thrown.expectMessage("Unable to parse source from the scheme type --> foobar");
		requirementSourceParser = TestUtil.initAbstractRequirementSourceParser(new URI("foobar://not_supported_scheme/resource.xml"), null);
		requirementSourceParser.getRequirements();
	}

	@Test
	public void testGetRequirementsBadFormattedScheme() throws ReqReportParserException, URISyntaxException {
		thrown.expect(URISyntaxException.class);
		TestUtil.initAbstractRequirementSourceParser(new URI("123badscheme://very_bad_scheme/resource.xml"), null);
	}

	@Test
	public void testGetRequirementsNullUri() throws ReqReportParserException, URISyntaxException {
		thrown.expectCause(Is.isA(NullPointerException.class));
		thrown.expectMessage("The URI to parse should is null");
		requirementSourceParser = TestUtil.initAbstractRequirementSourceParser(null, null);
		requirementSourceParser.getRequirements();
	}

	@Test
	public void testGetRequirementsVersion() throws ReqReportParserException {

		Collection<Requirement> reqs = requirementSourceParser.getRequirements("1.0");
		Assert.assertEquals(4, reqs.size());

		reqs = requirementSourceParser.getRequirements("1.1");
		Assert.assertEquals(1, reqs.size());
	}

	@Test
	public void testGetURI() throws URISyntaxException {
		final AbstractReqReportParser parser = (AbstractReqReportParser) requirementSourceParser;
		Assert.assertTrue(parser.getURI().toString().matches(Pattern.quote(TestUtil.REQUIREMENTS_INPUT_FILE1_XML_URI.toString())));
	}

	private void setUpByUsingUri(final URI uri) throws ReqReportParserException {
		this.requirementSourceParser = TestUtil.initAbstractRequirementSourceParser(uri, null);
	}
}
