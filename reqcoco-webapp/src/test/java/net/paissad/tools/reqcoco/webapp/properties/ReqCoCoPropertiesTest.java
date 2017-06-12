package net.paissad.tools.reqcoco.webapp.properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "reqcoco.banner=false", "reqcoco.server.port=9999" })
public class ReqCoCoPropertiesTest {

    @Autowired
    private ReqCoCoProperties reqCoCoProperties;

    @Test
    public void testLoadingAllProps() {
        Assert.assertFalse(reqCoCoProperties.isBanner());
        Assert.assertNotNull(reqCoCoProperties.getServer());
        Assert.assertEquals(9999, reqCoCoProperties.getServer().getPort());
    }

}
