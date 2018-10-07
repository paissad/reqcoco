package net.paissad.tools.reqcoco.parser.simple.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTag;

public class ReqDeclTagTest {

	private ReqDeclTag reqDeclTag;

	@Before
	public void setUp() throws Exception {
		this.reqDeclTag = new ReqDeclTag();
		this.reqDeclTag.setId("id");
		this.reqDeclTag.setVersion("ver");
		this.reqDeclTag.setRevision("rev");
		this.reqDeclTag.setSummary("desc");
		this.reqDeclTag.setLink("http://site.xyz");
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
	
    @Test
    public void testGetLink() {
        Assert.assertEquals("http://site.xyz", this.reqDeclTag.getLink());
    }
    
	@Test
	public void testEqualsAndHashCode() {

	    final ReqDeclTag reqDeclTag2 = new ReqDeclTag();
        final ReqDeclTag reqDeclTag3 = new ReqDeclTag();
        final ReqDeclTag reqDeclTag4 = new ReqDeclTag();
        final ReqDeclTag reqDeclTag5 = new ReqDeclTag();
        final ReqDeclTag reqDeclTag6 = new ReqDeclTag();
        final ReqDeclTag reqDeclTag7 = new ReqDeclTag();
        final ReqDeclTag reqDeclTag8 = new ReqDeclTag();
	    
        reqDeclTag2.setId("id");
        reqDeclTag2.setVersion("ver");
        reqDeclTag2.setRevision("rev");
        reqDeclTag2.setSummary("desc");

        reqDeclTag3.setId("id_3");
        reqDeclTag3.setVersion("ver");
        reqDeclTag3.setRevision("rev");

        reqDeclTag4.setId("id");
        reqDeclTag4.setVersion("ver");
        reqDeclTag4.setRevision("rev");
        reqDeclTag4.setSummary("desc_4");
        
        reqDeclTag5.setId("id");
        reqDeclTag5.setVersion("ver2");
        reqDeclTag5.setRevision("rev");
        
        reqDeclTag6.setId("id");
        reqDeclTag6.setVersion("ver2");
        
        reqDeclTag7.setId("id");
        reqDeclTag7.setVersion("rev");
        
        reqDeclTag8.setId("id");
        reqDeclTag8.setVersion("rev8");
        
        Assert.assertEquals(this.reqDeclTag, reqDeclTag2); // all fields are equal
        Assert.assertEquals(this.reqDeclTag.hashCode(), reqDeclTag2.hashCode());
     
        Assert.assertNotEquals(this.reqDeclTag, reqDeclTag3); // different id
        Assert.assertNotEquals(this.reqDeclTag.hashCode(), reqDeclTag3.hashCode());
        
        Assert.assertEquals(this.reqDeclTag, reqDeclTag4); // summary/description is supposed to be ignored
        Assert.assertEquals(this.reqDeclTag.hashCode(), reqDeclTag4.hashCode());
        
        Assert.assertNotEquals(reqDeclTag5, reqDeclTag6); // Revision is present in one declaration, but not on the other side
        Assert.assertNotEquals(reqDeclTag5.hashCode(), reqDeclTag6.hashCode());
        
        Assert.assertNotEquals(reqDeclTag6, reqDeclTag7); // different versions
        Assert.assertNotEquals(reqDeclTag6.hashCode(), reqDeclTag7.hashCode());
        
        Assert.assertNotEquals(this.reqDeclTag, reqDeclTag7); // version not present in one of the declarations
        Assert.assertNotEquals(this.reqDeclTag.hashCode(), reqDeclTag7.hashCode());
        
        Assert.assertNotEquals(reqDeclTag7, reqDeclTag8); // different revisions
        Assert.assertNotEquals(reqDeclTag7.hashCode(), reqDeclTag8.hashCode());        
	}
}
