package net.paissad.tools.reqcoco.parser.simple.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import net.paissad.tools.reqcoco.parser.simple.impl.FileReqSourceParser;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;

public class FileReqSourceParserTest {

	private FileReqSourceParser	reqSourceParser;

	private URI					uri;

	private ReqDeclTagConfig	declTagConfig;

	private Map<String, Object>	options;

	@Rule
	public ExpectedException	thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.reqSourceParser = new FileReqSourceParser();
		this.uri = this.getUriForStub("/samples/input/req_declarations_1.txt");
		this.declTagConfig = new SimpleReqDeclTagConfig();
		this.options = new HashMap<>();
	}

	@Test
	public void testParse() throws ReqSourceParserException {

		final Collection<Requirement> declaredRequirements = reqSourceParser.parse(uri, declTagConfig, options);
		Assert.assertNotNull(declaredRequirements);
		Assert.assertEquals(13, declaredRequirements.size());

		final Collection<Requirement> requirementsReq5 = getRequirementsHavingId("req_5", declaredRequirements);
		Assert.assertEquals(1, requirementsReq5.size());
		Requirement req5 = requirementsReq5.iterator().next();
		Assert.assertEquals("1.1", req5.getVersion());
		Assert.assertEquals("r2", req5.getRevision());
		Assert.assertEquals("There is some other content not processed ...", req5.getShortDescription());
	}

	@Test
	public void testParseUnexistentFile() throws ReqSourceParserException {

		this.uri = Paths.get("i_bet_this_file_does_not_exit").toUri();
		thrown.expect(Is.isA(ReqSourceParserException.class));
		thrown.expectMessage("I/O error while parsing the source for retrieving the declared requirements");
		reqSourceParser.parse(uri, declTagConfig, options);
	}

	private URI getUriForStub(final String stubResource) throws URISyntaxException {
		return FileReqSourceParserTest.class.getResource(stubResource).toURI();
	}

	private Collection<Requirement> getRequirementsHavingId(final String id, final Collection<Requirement> requirements) {
		return requirements.parallelStream().filter(req -> id.equals(req.getName())).collect(Collectors.toList());
	}

}
