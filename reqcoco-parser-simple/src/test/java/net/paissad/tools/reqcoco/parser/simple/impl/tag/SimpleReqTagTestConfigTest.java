package net.paissad.tools.reqcoco.parser.simple.impl.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqTagTestConfig;

public class SimpleReqTagTestConfigTest {

	private SimpleReqTagTestConfig	tagConfig;

	private String					tagStub;

	@Rule
	public ExpectedException		thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.tagConfig = new SimpleReqTagTestConfig();
		this.tagStub = "FOOBAR ... @ReqTestCode( id = \"req_1\", version = \"1.0\", revision = \"r1\",  author = \"myAuthor\",  comment = \"myComment\", whatever ...) i don't my the rest ...";
		this.tagStub += "@ReqTestCode	( id = \"req_2\", version = \"1.1\", revision = \"r2\",  author = \"myAuthor2\",  comment = \"myComment2\")";
	}

	@Test
	public void testGetCompleteRegexLineWithMoreThanOneTag() {

		final Pattern patternTag = Pattern.compile(this.tagConfig.getCompleteRegex());
		final Matcher matcherTag = patternTag.matcher(tagStub);

		matcherTag.find();
		final String extractedTag1 = matcherTag.group();
		Assert.assertEquals(
		        "@ReqTestCode( id = \"req_1\", version = \"1.0\", revision = \"r1\",  author = \"myAuthor\",  comment = \"myComment\", whatever ...)",
		        extractedTag1);

		matcherTag.find();
		final String extractedTag2 = matcherTag.group();
		Assert.assertEquals(
		        "@ReqTestCode	( id = \"req_2\", version = \"1.1\", revision = \"r2\",  author = \"myAuthor2\",  comment = \"myComment2\")",
		        extractedTag2);

	}

	@Test
	public void testGetCompleteRegexLineWithZeroTag() {

		this.tagStub = "There is no tag in here ...";

		final Pattern patternTag = Pattern.compile(this.tagConfig.getCompleteRegex());
		final Matcher matcherTag = patternTag.matcher(tagStub);

		matcherTag.find();

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No match found");

		matcherTag.group();
	}

}
