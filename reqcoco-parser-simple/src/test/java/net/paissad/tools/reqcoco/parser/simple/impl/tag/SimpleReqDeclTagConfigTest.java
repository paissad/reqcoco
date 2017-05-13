package net.paissad.tools.reqcoco.parser.simple.impl.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;

public class SimpleReqDeclTagConfigTest {

	private SimpleReqDeclTagConfig	tagConfig;

	private String					lineStub;

	private String					tagStub;

	@Before
	public void setUp() throws Exception {
		this.tagConfig = new SimpleReqDeclTagConfig();
		this.lineStub = "FOOBAR ... @Req( id = \"req_6\", version = \"1.0\", revision = \"r1\",  summary = \"short desc 1\", whatever ...) i don't mind the rest ...";
		this.lineStub += "@Req( id = \"req_7\", version = \"1.1\", revision = \"r2\")";
		this.tagStub = "@Req(id = \"req_9\", version = \"1.2\", revision = \"r3\", summary=\"dummy desc !!\")";
	}

	@Test
	public void testGetCompleteRegex() {

		final Pattern patternTag = Pattern.compile(this.tagConfig.getCompleteRegex());
		final Matcher matcherTag = patternTag.matcher(lineStub);

		matcherTag.find();
		final String extractedTag1 = matcherTag.group();
		Assert.assertEquals("@Req( id = \"req_6\", version = \"1.0\", revision = \"r1\",  summary = \"short desc 1\", whatever ...)", extractedTag1);

		matcherTag.find();
		final String extractedTag2 = matcherTag.group();
		Assert.assertEquals("@Req( id = \"req_7\", version = \"1.1\", revision = \"r2\")", extractedTag2);
	}

	@Test
	public void testGetIdRegex() {
		final Pattern patternId = Pattern.compile(this.tagConfig.getIdRegex());
		final Matcher matcherId = patternId.matcher(tagStub);

		matcherId.find();
		Assert.assertEquals("req_9", matcherId.group(1));
	}

	@Test
	public void testGetVersionRegex() {
		final Pattern patternVersion = Pattern.compile(this.tagConfig.getVersionRegex());
		final Matcher matcherVersion = patternVersion.matcher(tagStub);

		matcherVersion.find();
		Assert.assertEquals("1.2", matcherVersion.group(1));
	}

	@Test
	public void testGetRevisionRegex() {
		final Pattern patternRevision = Pattern.compile(this.tagConfig.getRevisionRegex());
		final Matcher matcherRevision = patternRevision.matcher(tagStub);

		matcherRevision.find();
		Assert.assertEquals("r3", matcherRevision.group(1));
	}

	@Test
	public void testGetSummaryRegex() {
		final Pattern patternSummary = Pattern.compile(this.tagConfig.getSummaryRegex());
		final Matcher matcherSummary = patternSummary.matcher(tagStub);

		matcherSummary.find();
		Assert.assertEquals("dummy desc !!", matcherSummary.group(1));
	}

}
