package net.paissad.tools.reqcoco.api.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RequirementsTest {

	private Requirements requirements;

	@Before
	public void setUp() throws Exception {
		this.requirements = new Requirements();
	}

	@Test
	public void testGetRequirements() {
		Assert.assertNotNull(this.requirements.getRequirements());
		Assert.assertTrue(this.requirements.getRequirements().isEmpty());
	}

	@Test
	public void testGetById() {
		this.requirements.getRequirements().add(new Requirement("id1", "1.0", "r1"));
		this.requirements.getRequirements().add(new Requirement("id2", "1.0", "r1"));
		this.requirements.getRequirements().add(new Requirement("id3", "1.0", null));
		this.requirements.getRequirements().add(new Requirement("id1", "2.0", "r1"));

		Assert.assertEquals(2, Requirements.getByName(requirements.getRequirements(), "id1").size());
		Assert.assertEquals(1, Requirements.getByName(requirements.getRequirements(), "id2").size());
	}

	@Test
	public void testGetByVersion() {
		this.requirements.getRequirements().add(new Requirement("id1", "1.0", "r1"));
		this.requirements.getRequirements().add(new Requirement("id2", "1.0", "r1"));
		this.requirements.getRequirements().add(new Requirement("id3", "1.0", null));
		this.requirements.getRequirements().add(new Requirement("id4", "2.0", "r1"));

		Assert.assertEquals(3, Requirements.getByVersion(requirements.getRequirements(), "1.0").size());
		Assert.assertEquals(1, Requirements.getByVersion(requirements.getRequirements(), "2.0").size());
	}

}
