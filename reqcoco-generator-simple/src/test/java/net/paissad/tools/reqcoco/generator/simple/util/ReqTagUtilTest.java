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

}
