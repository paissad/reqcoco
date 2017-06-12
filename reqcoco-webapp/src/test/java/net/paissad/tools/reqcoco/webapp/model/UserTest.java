package net.paissad.tools.reqcoco.webapp.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private User user;

    @Before
    public void setUp() throws Exception {
        this.user = new User();
        this.user.setId(3L);
        this.user.setLogin("dummy_login");
        this.user.setPassword("hidden");
        this.user.setEmail("aa@bb.cc");
        this.user.setFullName("firstname lastname");
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(3L, this.user.getId().longValue());
    }

    @Test
    public void testGetLogin() {
        Assert.assertEquals("dummy_login", this.user.getLogin());
    }

    @Test
    public void testGetPassword() {
        Assert.assertEquals("hidden", this.user.getPassword());
    }

    @Test
    public void testGetEmail() {
        Assert.assertEquals("aa@bb.cc", this.user.getEmail());
    }

    @Test
    public void testGetFullName() {
        Assert.assertEquals("firstname lastname", this.user.getFullName());
    }

}
