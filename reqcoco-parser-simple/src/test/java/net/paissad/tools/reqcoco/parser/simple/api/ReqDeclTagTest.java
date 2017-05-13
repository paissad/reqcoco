package net.paissad.tools.reqcoco.parser.simple.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTag;

public class ReqDeclTagTest {

	private ReqDeclTag reqDeclTag;

	@Before
	public void setUp() throws Exception {
		this.reqDeclTag = new ReqDeclTag();
		this.reqDeclTag.setId("id");
		this.reqDeclTag.setVersion("ver");
		this.reqDeclTag.setRevision("rev");
		this.reqDeclTag.setSummary("desc");
	}

	@Test
	public void testGetId() {
		Assert.assertEquals("id", this.reqDeclTag.getId());
	}

	@Test
	public void testGetVersion() {
		Assert.assertEquals("ver", this.reqDeclTag.getVersion());
	}

	@Test
	public void testGetRevision() {
		Assert.assertEquals("rev", this.reqDeclTag.getRevision());
	}

	@Test
	public void testGetSummary() {
		Assert.assertEquals("desc", this.reqDeclTag.getSummary());
	}

}
