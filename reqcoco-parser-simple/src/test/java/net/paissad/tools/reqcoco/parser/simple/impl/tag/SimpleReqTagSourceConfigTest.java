package net.paissad.tools.reqcoco.parser.simple.impl.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqTagSourceConfig;

public class SimpleReqTagSourceConfigTest {

	private SimpleReqTagSourceConfig	tagConfig;

	/** Stub to parse in order to retrieve some tags .. */
	private String						stub;

	@Rule
	public ExpectedException			thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.tagConfig = new SimpleReqTagSourceConfig();
		this.stub = "FOOBAR ... @ReqSourceCode( id = \"req_1\", version = \"1.0\", revision = \"r1\",  author = \"myAuthor\",  comment = \"myComment\", whatever ...) i don't my the rest ...";
		this.stub += "@ReqSourceCode ( id = \"req_2\", version = \"1.1\", revision = \"r2\",  author = \"myAuthor2\",  comment = \"myComment2\")";
	}

	@Test
	public void testGetCompleteRegexLineWithMoreThanOneTag() {

		final Pattern patternTag = Pattern.compile(this.tagConfig.getCompleteRegex());
		final Matcher matcherTag = patternTag.matcher(stub);

		matcherTag.find();
		final String extractedTag1 = matcherTag.group();
		Assert.assertEquals(
		        "@ReqSourceCode( id = \"req_1\", version = \"1.0\", revision = \"r1\",  author = \"myAuthor\",  comment = \"myComment\", whatever ...)",
		        extractedTag1);

		matcherTag.find();
		final String extractedTag2 = matcherTag.group();
		Assert.assertEquals(
		        "@ReqSourceCode ( id = \"req_2\", version = \"1.1\", revision = \"r2\",  author = \"myAuthor2\",  comment = \"myComment2\")",
		        extractedTag2);

	}

	@Test
	public void testGetCompleteRegexLineWithZeroTag() {

		this.stub = "There is no tag in here ...";

		final Pattern patternTag = Pattern.compile(this.tagConfig.getCompleteRegex());
		final Matcher matcherTag = patternTag.matcher(stub);

		matcherTag.find();

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No match found");

		matcherTag.group();
	}

}
