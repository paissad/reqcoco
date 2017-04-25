package net.paissad.tools.reqcoco.api.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VersionTest {

	private Version version;

	@Before
	public void setUp() throws Exception {
		version = new Version();
	}

	@Test
	public void testVersionUnknown() {
		Assert.assertEquals("__unknown__", Version.UNKNOWN.getValue());
	}

	@Test
	public void testVersionAny() {
		Assert.assertEquals("__any__", Version.ANY.getValue());
	}

	@Test
	public void testGetAndSetValue() {
		version.setValue("1.5");
		Assert.assertEquals("1.5", version.getValue());
	}

	@Test
	public void testGetAndSetRevision() {
		Assert.assertNull(version.getRevision());
		Revision rev = new Revision();
		rev.setValue("r4");
		version.setRevision(rev);
		Assert.assertEquals(rev, version.getRevision());
	}

	@Test
	public void testToString() {
		version.toString();
	}

}
