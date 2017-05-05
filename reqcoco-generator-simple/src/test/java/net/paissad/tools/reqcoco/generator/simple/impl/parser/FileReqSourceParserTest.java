package net.paissad.tools.reqcoco.generator.simple.impl.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.generator.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqSourceParserException;
import net.paissad.tools.reqcoco.generator.simple.impl.tag.SimpleReqDeclTagConfig;

public class FileReqSourceParserTest {

	private FileReqSourceParser	reqSourceParser;

	private URI					uri;

	private ReqDeclTagConfig	declTagConfig;

	private Map<String, Object>	options;

	@Before
	public void setUp() throws Exception {
		this.reqSourceParser = new FileReqSourceParser();
		this.uri = this.getUriForStub("/samples/input/req_declarations_1.txt");
		this.declTagConfig = new SimpleReqDeclTagConfig();
		this.options = new HashMap<>();
	}

	// TODO: improve test by checking all requirements one by one !
	@Test
	public void testParse() throws ReqSourceParserException {

		final Collection<Requirement> declaredRequirements = reqSourceParser.parse(uri, declTagConfig, options);
		Assert.assertNotNull(declaredRequirements);
		Assert.assertEquals(9, declaredRequirements.size());

		final Collection<Requirement> requirementsReq5 = getRequirementsHavingId("req_5", declaredRequirements);
		Assert.assertEquals(1, requirementsReq5.size());
		Requirement req5 = requirementsReq5.iterator().next();
		Assert.assertEquals("1.1", req5.getVersion());
		Assert.assertEquals("r2", req5.getRevision());
		Assert.assertEquals("There is some other content not processed ...", req5.getShortDescription());
	}

	private URI getUriForStub(final String stubResource) throws URISyntaxException {
		return FileReqSourceParserTest.class.getResource(stubResource).toURI();
	}

	private Collection<Requirement> getRequirementsHavingId(final String id, final Collection<Requirement> requirements) {
		return requirements.parallelStream().filter(req -> id.equals(req.getId())).collect(Collectors.toList());
	}

}
