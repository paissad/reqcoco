package net.paissad.tools.reqcoco.api.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RequirementTest {

	private Requirement requirement;

	@Before
	public void setUp() throws Exception {
		this.requirement = new Requirement();
		this.requirement.setId("myId");
		this.requirement.setVersion("myVersion");
		this.requirement.setRevision("myRevision");
		this.requirement.setShortDescription("myShortDesc");
		this.requirement.setFullDescription("myLongDesc");
		this.requirement.setCodeDone(true);
		this.requirement.setCodeAuthor("developer1");
		this.requirement.setCodeAuthorComment("finished");
		this.requirement.setTestDone(false);
		this.requirement.setTestAuthor("dev2");
		this.requirement.setTestAuthorComment("wip ...");
		this.requirement.setIgnore(true);
		this.requirement.setLink("http://foobar");
	}

	@Test
	public void testGetId() {
		Assert.assertEquals("myId", this.requirement.getId());
	}

	@Test
	public void testIsIgnore() {
		Assert.assertTrue(this.requirement.isIgnore());
	}

	@Test
	public void testGetShortDescription() {
		Assert.assertEquals("myShortDesc", this.requirement.getShortDescription());
	}

	@Test
	public void testGetFullDescription() {
		Assert.assertEquals("myLongDesc", this.requirement.getFullDescription());
	}

	@Test
	public void testGetVersion() {
		Assert.assertEquals("myVersion", this.requirement.getVersion());
	}

	@Test
	public void testGetRevision() {
		Assert.assertEquals("myRevision", this.requirement.getRevision());
	}

	@Test
	public void testIsCodeDone() {
		Assert.assertTrue(this.requirement.isCodeDone());
	}

	@Test
	public void testGetCodeAuthor() {
		Assert.assertEquals("developer1", this.requirement.getCodeAuthor());
	}

	@Test
	public void testGetCodeAuthorComment() {
		Assert.assertEquals("finished", this.requirement.getCodeAuthorComment());
	}

	@Test
	public void testIsTestDone() {
		Assert.assertFalse(this.requirement.isTestDone());
	}

	@Test
	public void testGetTestAuthor() {
		Assert.assertEquals("dev2", this.requirement.getTestAuthor());
	}

	@Test
	public void testGetTestAuthorComment() {
		Assert.assertEquals("wip ...", this.requirement.getTestAuthorComment());
	}

	@Test
	public void testGetLink() {
		Assert.assertEquals("http://foobar", this.requirement.getLink());
	}

	@Test
	public void testToString() {
		this.requirement.toString();
	}

}
