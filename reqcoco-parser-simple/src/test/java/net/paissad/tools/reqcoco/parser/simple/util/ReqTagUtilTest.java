package net.paissad.tools.reqcoco.parser.simple.util;

import org.junit.Assert;
import org.junit.Test;

import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.util.ReqTagUtil;

public class ReqTagUtilTest {

    @Test
    public void testExtractFieldValueWithAMatch() {
        Assert.assertEquals("bar", ReqTagUtil.extractFieldValue("input_foo_bar_baz", ".*(bar).*", 1));
    }

    @Test
    public void testExtractFieldValueWhenTherIsNoMatch() {
        Assert.assertNull(ReqTagUtil.extractFieldValue("input", "regex", 1));
    }

    @Test
    public void testTrimString() {
        Assert.assertNull(ReqTagUtil.trimString(null));
        Assert.assertTrue(ReqTagUtil.trimString("   ").isEmpty());
        Assert.assertEquals("i have a tab & space before and after", ReqTagUtil.trimString(" 	i have a tab & space before and after   	"));
        Assert.assertEquals("I am preprended and appended by a horizontal space", ReqTagUtil.trimString(" I am preprended and appended by a horizontal space "));
    }

    @Test
    public void testUnEscapeString() {
        Assert.assertNull(ReqTagUtil.unEscapeString(null));

        Assert.assertEquals("Error decoding XML", "@Req(id = \"id\", version = \"ver\", revision = \"rev\")",
                ReqTagUtil.unEscapeString("@Req(id = &quot;id&quot;, version = &quot;ver&quot;, revision = &quot;rev&quot;)"));

        Assert.assertEquals("Error decoding HTML", "@Req(id = \"id\", version = \"ver\", revision = \"rev\")",
                ReqTagUtil.unEscapeString("@Req(id = &#34;id&#34;, version = &#34;ver&#34;, revision = &#34;rev&#34;)"));
    }

    @Test
    public void testStripTagAndTrim() {
        Assert.assertEquals("foobar", ReqTagUtil.stripTagAndTrim("  @Req(key=val) foobar  ", new SimpleReqDeclTagConfig()));
    }
}
