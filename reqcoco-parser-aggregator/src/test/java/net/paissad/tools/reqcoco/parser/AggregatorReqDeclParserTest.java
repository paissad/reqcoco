package net.paissad.tools.reqcoco.parser;

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
import net.paissad.tools.reqcoco.parser.aggregator.AggregatorReqDeclParser;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.exception.ReqParserException;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.spi.ReqDeclParserProvider;

public class AggregatorReqDeclParserTest {

    private AggregatorReqDeclParser reqSourceParser;

    private URI                     uri;

    private ReqDeclTagConfig        declTagConfig;

    @Rule
    public ExpectedException        thrown = ExpectedException.none();

    private HashMap<String, Object> options;

    @Before
    public void setUp() throws Exception {
        this.reqSourceParser = new AggregatorReqDeclParser();
        this.uri = this.getUriForStub("/samples/input/declarations");
        this.declTagConfig = new SimpleReqDeclTagConfig();
        this.options = new HashMap<>();
    }

    @Test
    public void testGetIdentitier() {
        Assert.assertEquals(AggregatorReqDeclParser.PARSER_IDENTIFIER, this.reqSourceParser.getIdentitier());
    }

    @Test
    public void testgetRegisteredFileExtensions() {
        Assert.assertNull(this.reqSourceParser.getRegisteredFileExtensions());
    }

    @Test
    public void testGetParserForFileExtension_docx() {
        Assert.assertNotEquals(AggregatorReqDeclParser.class, ReqDeclParserProvider.getInstance().getParserForFileExtension(".foobar").getClass());
    }

    @Test
    public void testParse() throws ReqParserException, URISyntaxException {

        final Collection<Requirement> declaredRequirements = reqSourceParser.parse(uri, declTagConfig, options);

        Assert.assertNotNull(declaredRequirements);

        Assert.assertEquals(4, declaredRequirements.size());

        final Collection<Requirement> requirementsReq99 = getRequirementsHavingId("req_99", declaredRequirements);
        Assert.assertEquals(1, requirementsReq99.size());
        Requirement req99 = requirementsReq99.iterator().next();
        Assert.assertEquals("1.0", req99.getVersion());
        Assert.assertEquals("r1", req99.getRevision());
        Assert.assertEquals("short summary n°99", req99.getShortDescription());
    }

    @Test
    public void testParseUnexistentResource() throws ReqParserException {

        this.uri = Paths.get("unexistent_resoure").toUri();
        thrown.expect(Is.isA(ReqParserException.class));
        reqSourceParser.parse(uri, declTagConfig, options);
    }

    private URI getUriForStub(final String stubResource) throws URISyntaxException {
        return getClass().getResource(stubResource).toURI();
    }

    private Collection<Requirement> getRequirementsHavingId(final String id, final Collection<Requirement> requirements) {
        return requirements.parallelStream().filter(req -> id.equals(req.getName())).collect(Collectors.toList());
    }

}
