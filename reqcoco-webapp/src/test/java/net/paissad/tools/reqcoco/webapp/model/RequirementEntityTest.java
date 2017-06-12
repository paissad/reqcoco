package net.paissad.tools.reqcoco.webapp.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RequirementEntityTest {

    private RequirementEntity reqEntity;

    @Before
    public void setUp() throws Exception {
        this.reqEntity = new RequirementEntity();
        this.reqEntity.setId(1L);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(1L, this.reqEntity.getId().longValue());
    }

}
