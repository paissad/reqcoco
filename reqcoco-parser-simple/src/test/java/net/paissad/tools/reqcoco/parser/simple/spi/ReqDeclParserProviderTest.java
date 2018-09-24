package net.paissad.tools.reqcoco.parser.simple.spi;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import net.paissad.tools.reqcoco.parser.simple.impl.FileReqDeclParser;

public class ReqDeclParserProviderTest {

    @Test
    public void testGetInstance() {
        Assert.assertNotNull(ReqDeclParserProvider.getInstance());
    }

    @Test
    public void testGetRegisteredParsers() {
        final Collection<ReqDeclParser> registeredParsers = ReqDeclParserProvider.getInstance().getRegisteredParsers();
        Assert.assertTrue(registeredParsers.size() == 1);
        Assert.assertTrue(registeredParsers.stream().anyMatch(p -> p.getClass().equals(FileReqDeclParser.class)));
    }

    @Test
    public void testGetParserHavingExistingIdentifier() {
        Assert.assertEquals(ReqDeclParserProvider.getInstance().getParserHavingIdentifier(FileReqDeclParser.PARSER_IDENTIFIER).getClass(), FileReqDeclParser.class);
    }

    @Test
    public void testGetParserHavingUnknownIdentifier() {
        Assert.assertNull(ReqDeclParserProvider.getInstance().getParserHavingIdentifier("__unknonwn_identifier__"));
    }

    @Test
    public void testGetParserForFileExtension_NotRegisteredExtension() {
        Assert.assertEquals(FileReqDeclParser.class, ReqDeclParserProvider.getInstance().getParserForFileExtension(".__i_am_not_registered").getClass());
    }

    @Test
    public void testGetParserForFileExtension_NullValue() {
        Assert.assertEquals(FileReqDeclParser.class, ReqDeclParserProvider.getInstance().getParserForFileExtension(null).getClass());
    }

}
