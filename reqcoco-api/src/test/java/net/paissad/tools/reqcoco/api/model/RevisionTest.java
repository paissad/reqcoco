package net.paissad.tools.reqcoco.api.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RevisionTest {

	private Revision rev;

	@Before
	public void setUp() throws Exception {
		rev = new Revision();
	}

	@Test
	public void testGetterAndSetter() {
		rev.setValue("r7");
		Assert.assertEquals("r7", rev.getValue());
	}

	@Test
	public void testToString() {
		rev.toString();
	}

}
