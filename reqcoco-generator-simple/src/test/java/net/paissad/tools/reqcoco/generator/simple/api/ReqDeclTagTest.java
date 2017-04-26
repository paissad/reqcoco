package net.paissad.tools.reqcoco.generator.simple.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
