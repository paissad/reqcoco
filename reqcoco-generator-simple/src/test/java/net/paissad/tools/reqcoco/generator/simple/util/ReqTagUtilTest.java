package net.paissad.tools.reqcoco.generator.simple.util;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertEquals("I am preprended and appended by a horizontal space",
		        ReqTagUtil.trimString(" I am preprended and appended by a horizontal space "));
	}
}
