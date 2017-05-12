package net.paissad.tools.reqcoco.generator.simple.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.paissad.tools.reqcoco.generator.simple.api.ReqDeclTag;

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

	@Test
	public void testBuildReqDeclTagFromMembers() {

		final Map<String, String> tagMembers = new HashMap<>();
		final ReqDeclTag expectedTag = new ReqDeclTag();

		tagMembers.put("id", "my_id");
		tagMembers.put("version", "v1.0");
		tagMembers.put("revision", "r13");
		tagMembers.put("summary", "my summary");

		expectedTag.setId("my_id");
		expectedTag.setVersion("v1.0");
		expectedTag.setRevision("r13");
		expectedTag.setSummary("my summary");

		Assert.assertEquals(expectedTag, ReqTagUtil.buildReqDeclTagFromMembers(tagMembers));

		tagMembers.put("summary", "diff");
		Assert.assertNotEquals(expectedTag, ReqTagUtil.buildReqDeclTagFromMembers(tagMembers));

		tagMembers.put("summary", null);
		Assert.assertNotEquals(expectedTag, ReqTagUtil.buildReqDeclTagFromMembers(tagMembers));

		tagMembers.remove("summary");
		Assert.assertNotEquals(expectedTag, ReqTagUtil.buildReqDeclTagFromMembers(tagMembers));
	}
}
