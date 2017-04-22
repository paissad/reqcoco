package net.paissad.tools.reqcoco.core.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.regex.Pattern;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.api.exception.ReParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.api.model.Version;
import net.paissad.tools.reqcoco.api.parser.ReqSourceParser;
import net.paissad.tools.reqcoco.core.TestUtil;

public class AbstractReqSourceParserTest {

	private ReqSourceParser		requirementSourceParser;

	@Rule
	public ExpectedException	thrown	= ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.requirementSourceParser = TestUtil.initAbstractRequirementSourceParser(TestUtil.REQUIREMENTS_INPUT_FILE1_XML_URI, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetRequirements() throws ReParserException {
		final Requirements reqs = requirementSourceParser.getRequirements();
		Assert.assertNotNull(reqs);
		Assert.assertEquals(3, reqs.getRequirements().stream().count());
	}

	@Test
	public void testGetRequirementsNotSupportedUriScheme() throws ReParserException, URISyntaxException {
		thrown.expectCause(Is.isA(UnsupportedOperationException.class));
		thrown.expectMessage("Unable to parse source from the scheme type --> foobar");
		requirementSourceParser = TestUtil.initAbstractRequirementSourceParser(new URI("foobar://not_supported_scheme/resource.xml"), null);
		requirementSourceParser.getRequirements();
	}

	@Test
	public void testGetRequirementsBadFormattedScheme() throws ReParserException, URISyntaxException {
		thrown.expect(URISyntaxException.class);
		TestUtil.initAbstractRequirementSourceParser(new URI("123badscheme://very_bad_scheme/resource.xml"), null);
	}

	@Test
	public void testGetRequirementsNullUri() throws ReParserException, URISyntaxException {
		thrown.expectCause(Is.isA(NullPointerException.class));
		thrown.expectMessage("The URI to parse should is null");
		requirementSourceParser = TestUtil.initAbstractRequirementSourceParser(null, null);
		requirementSourceParser.getRequirements();
	}

	@Test
	public void testGetRequirementsVersion() throws ReParserException {
		final Version v1 = new Version();
		v1.setValue("1.0");
		Collection<Requirement> reqs = requirementSourceParser.getRequirements(v1);
		Assert.assertEquals(2, reqs.size());

		final Version v1_1 = new Version();
		v1_1.setValue("1.1");
		reqs = requirementSourceParser.getRequirements(v1_1);
		Assert.assertEquals(1, reqs.size());
	}

	@Test
	public void testGetURI() throws URISyntaxException {
		final AbstractReqSourceParser parser = (AbstractReqSourceParser) requirementSourceParser;
		Assert.assertTrue(parser.getURI().toString().matches(Pattern.quote(TestUtil.REQUIREMENTS_INPUT_FILE1_XML_URI.toString())));
	}

}
