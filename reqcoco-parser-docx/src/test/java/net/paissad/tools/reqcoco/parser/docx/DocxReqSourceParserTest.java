package net.paissad.tools.reqcoco.parser.docx;

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
import net.paissad.tools.reqcoco.parser.simple.exception.ReqParserException;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.spi.ReqDeclParserProvider;

public class DocxReqSourceParserTest {

	private DocxReqDeclParser		docxReqSourceParser;

	private URI						uri;

	private ReqDeclTagConfig		declTagConfig;

	@Rule
	public ExpectedException		thrown	= ExpectedException.none();

	private HashMap<String, Object>	options;

	@Before
	public void setUp() throws Exception {
		this.docxReqSourceParser = new DocxReqDeclParser();
		this.uri = this.getUriForStub("/samples/input/req_declarations_1.docx");
		this.declTagConfig = new SimpleReqDeclTagConfig();
		this.options = new HashMap<>();
	}

    @Test
    public void testGetIdentitier() {
        Assert.assertEquals(DocxReqDeclParser.PARSER_IDENTIFIER, this.docxReqSourceParser.getIdentitier());
    }

    @Test
    public void testgetRegisteredFileExtensions() {
        Assert.assertTrue(this.docxReqSourceParser.getRegisteredFileExtensions().size() == 1);
        Assert.assertEquals(".docx", this.docxReqSourceParser.getRegisteredFileExtensions().iterator().next());
    }

    @Test
    public void testGetParserForFileExtension_docx() {
        Assert.assertEquals(DocxReqDeclParser.class, ReqDeclParserProvider.getInstance().getParserForFileExtension(".DOcx").getClass());
    }

	@Test
	public void testParse() throws ReqParserException {

		final Collection<Requirement> extractedRequirements = this.docxReqSourceParser.parse(uri, declTagConfig, options);
		Assert.assertEquals(1, extractedRequirements.size());
		final Collection<Requirement> reqs_1 = getRequirementsHavingName("req_1", extractedRequirements);
		Assert.assertEquals(1, reqs_1.size());
		Assert.assertEquals("desc 1 …", reqs_1.iterator().next().getShortDescription());
	}
	
	@Test
	public void testParseUnexistentFile() throws ReqParserException {

		this.uri = Paths.get("__unexistent_docx_file__").toUri();
		this.thrown.expect(Is.isA(ReqParserException.class));
		this.thrown.expectMessage("Error while reading the docx file : ");
		this.docxReqSourceParser.parse(uri, declTagConfig, options);
	}

	private URI getUriForStub(final String stubResource) throws URISyntaxException {
		return getClass().getResource(stubResource).toURI();
	}

	private Collection<Requirement> getRequirementsHavingName(final String name, final Collection<Requirement> requirements) {
		return requirements.parallelStream().filter(req -> name.equals(req.getName())).collect(Collectors.toList());
	}
}
