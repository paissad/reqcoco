package net.paissad.tools.reqcoco.webapp.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RoleTest {

    private Role role;

    @Before
    public void setUp() throws Exception {
        this.role = new Role();
        this.role.setId(2L);
        this.role.setName("dummy_role");
        this.role.setType(RoleType.CUSTOM);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(2L, this.role.getId().longValue());
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("dummy_role", this.role.getName());
    }

    @Test
    public void testGetType() {
        Assert.assertEquals(RoleType.CUSTOM, this.role.getType());
    }

}
