package net.paissad.tools.reqcoco.parser.simple.impl.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.parser.simple.impl.tag.AbstractReqCodeTagConfig;

public class AbstractReqCodeTagConfigTest {

	private AbstractReqCodeTagConfig	tagConfig;

	/** A string representing a well formatted tag */
	private String					tagStub;

	@Rule
	public ExpectedException		thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.tagConfig = new AbstractReqCodeTagConfig() {

			@Override
			public String getCompleteRegex() {
				return null;
			}
		};

		// Note the 'FOOBAR' into the stub ...
		this.tagStub = "@ReqFOOBARCode( id = \"req_1\", version = \"1.0\", revision = \"r1\",  author = \"myAuthor\",  comment = \"myComment\", whatever ...)";
	}

	@Test
	public void testGetIdRegex() {

		final Pattern patternId = Pattern.compile(this.tagConfig.getIdRegex());
		final Matcher matcherId = patternId.matcher(tagStub);

		matcherId.find();
		Assert.assertEquals("req_1", matcherId.group(1));
	}

	@Test
	public void testGetVersionRegex() {

		final Pattern patternVersion = Pattern.compile(this.tagConfig.getVersionRegex());
		final Matcher matcherVersion = patternVersion.matcher(tagStub);

		matcherVersion.find();
		Assert.assertEquals("1.0", matcherVersion.group(1));
	}

	@Test
	public void testGetRevisionRegex() {

		final Pattern patternRevision = Pattern.compile(this.tagConfig.getRevisionRegex());
		final Matcher matcherRevision = patternRevision.matcher(tagStub);

		matcherRevision.find();
		Assert.assertEquals("r1", matcherRevision.group(1));
	}

	@Test
	public void testGetAuthorRegex() {

		final Pattern patternAuthor = Pattern.compile(this.tagConfig.getAuthorRegex());
		final Matcher matcherAuthor = patternAuthor.matcher(tagStub);

		matcherAuthor.find();
		Assert.assertEquals("myAuthor", matcherAuthor.group(1));
	}

	@Test
	public void testGetCommentRegex() {

		final Pattern patternComment = Pattern.compile(this.tagConfig.getCommentRegex());
		final Matcher matcherComment = patternComment.matcher(tagStub);

		matcherComment.find();
		Assert.assertEquals("myComment", matcherComment.group(1));
	}

}
