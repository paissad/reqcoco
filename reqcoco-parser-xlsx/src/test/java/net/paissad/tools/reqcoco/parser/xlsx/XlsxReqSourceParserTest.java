package net.paissad.tools.reqcoco.parser.xlsx;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.xlsx.XlsxReqSourceParser;

public class XlsxReqSourceParserTest {

	private XlsxReqSourceParser		xlsxReqSourceParser;

	private URI						uri;

	private ReqDeclTagConfig		declTagConfig;

	@Rule
	public ExpectedException		thrown	= ExpectedException.none();

	private HashMap<String, Object>	options;

	@Before
	public void setUp() throws Exception {
		this.xlsxReqSourceParser = new XlsxReqSourceParser();
		this.uri = this.getUriForStub("/samples/input/req_declarations_1.xlsx");
		this.declTagConfig = new SimpleReqDeclTagConfig();
		this.options = new HashMap<>();
	}

	@Test
	public void testParse() throws ReqSourceParserException {

		final Collection<Requirement> extractedRequirements = this.xlsxReqSourceParser.parse(uri, declTagConfig, options);
		Assert.assertEquals(1, extractedRequirements.size());
		final Collection<Requirement> reqs_1 = getRequirementsHavingId("req_14", extractedRequirements);
		Assert.assertEquals(1, reqs_1.size());
		Assert.assertEquals("req_14 desc !", reqs_1.iterator().next().getShortDescription());
	}

	@Test
	public void testParseUnexistentFile() throws ReqSourceParserException {

		this.uri = Paths.get("__unexistent_xlsx_file__").toUri();
		this.thrown.expect(Is.isA(ReqSourceParserException.class));
		this.thrown.expectMessage("Error while reading the xlsx file : ");
		this.xlsxReqSourceParser.parse(uri, declTagConfig, options);
	}

	private URI getUriForStub(final String stubResource) throws URISyntaxException {
		return getClass().getResource(stubResource).toURI();
	}

	private Collection<Requirement> getRequirementsHavingId(final String id, final Collection<Requirement> requirements) {
		return requirements.parallelStream().filter(req -> id.equals(req.getName())).collect(Collectors.toList());
	}
}
