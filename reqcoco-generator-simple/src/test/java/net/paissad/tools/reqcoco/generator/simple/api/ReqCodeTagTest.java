package net.paissad.tools.reqcoco.generator.simple.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReqCodeTagTest {

	private ReqCodeTag reqTag;

	@Before
	public void setUp() throws Exception {
		this.reqTag = new ReqCodeTag();
		this.reqTag.setId("req_3");
		this.reqTag.setVersion("1.2");
		this.reqTag.setAuthor("myAuthor");
		this.reqTag.setComment("My comment");
	}

	@Test
	public void testGetId() {
		Assert.assertEquals("req_3", this.reqTag.getId());
	}

	@Test
	public void testGetVersion() {
		Assert.assertEquals("1.2", this.reqTag.getVersion());
	}

	@Test
	public void testGetAuthor() {
		Assert.assertEquals("myAuthor", this.reqTag.getAuthor());
	}

	@Test
	public void testGetComment() {
		Assert.assertEquals("My comment", this.reqTag.getComment());
	}

}
