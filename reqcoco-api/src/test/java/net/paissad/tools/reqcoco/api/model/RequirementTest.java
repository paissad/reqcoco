package net.paissad.tools.reqcoco.api.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lombok.Getter;

public class RequirementTest {

    private Requirement requirement;

    @Before
    public void setUp() throws Exception {
        this.requirement = new Requirement();
        this.requirement.setName("myId");
        this.requirement.setGroup("myGroup");
        this.requirement.setVersion("myVersion");
        this.requirement.setRevision("myRevision");
        this.requirement.setShortDescription("myShortDesc");
        this.requirement.setFullDescription("myLongDesc");
        this.requirement.setCodeStatus(Status.DONE);
        this.requirement.setCodeAuthor("developer1");
        this.requirement.setCodeAuthorComment("finished");
        this.requirement.setTestStatus(Status.TODO);
        this.requirement.setTestAuthor("dev2");
        this.requirement.setTestAuthorComment("wip ...");
        this.requirement.setLink("http://foobar");
    }

    @Test
    public void testGetId() {
        Assert.assertEquals("myId", this.requirement.getName());
    }

    @Test
    public void testGetGroup() {
        Assert.assertEquals("myGroup", this.requirement.getGroup());
    }

    @Test
    public void testIsIgnore() {
        this.requirement.setIgnore(true);
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
    public void testIsCodeDone_when_done() {
        Assert.assertTrue(this.requirement.isCodeDone());
    }

    @Test
    public void testIsCodeDone_when_not_done() {
        final Requirement req2 = new Requirement();
        req2.setCodeStatus(Status.IN_PROGRESS);
        Assert.assertFalse(req2.isCodeDone());
    }

    @Test
    public void testIsCodeIgnored_when_not_ignored() {
        Assert.assertFalse(this.requirement.isCodeIgnore());
    }

    @Test
    public void testIsCodeIgnored_when_ignored() {
        final Requirement req2 = new Requirement();
        req2.setCodeStatus(Status.IGNORE);
        Assert.assertTrue(req2.isCodeIgnore());
    }

    @Test
    public void testCodeStatus() {
        Assert.assertEquals(Status.DONE, this.requirement.getCodeStatus());
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
    public void testIsTestDone_when_not_done() {
        Assert.assertFalse(this.requirement.isTestDone());
    }

    @Test
    public void testIsTestDone_when_done() {
        final Requirement req3 = new Requirement();
        req3.setTestStatus(Status.DONE);
        Assert.assertTrue(req3.isTestDone());
    }

    @Test
    public void testIsTestIgnored_when_not_ignored() {
        Assert.assertFalse(this.requirement.isTestIgnore());
    }

    @Test
    public void testIsTestIgnored_when_ignored() {
        final Requirement req2 = new Requirement();
        req2.setTestStatus(Status.IGNORE);
        Assert.assertTrue(req2.isTestIgnore());
    }

    @Test
    public void testTestStatus() {
        Assert.assertEquals(Status.TODO, this.requirement.getTestStatus());
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

    @Test
    public void testHashCode() {

        final Requirement req1 = new Requirement("id", "ver", "rev");
        Requirement req2 = new Requirement("id", "ver", "rev");

        Assert.assertEquals(req1.hashCode(), req2.hashCode());

        req2 = new Requirement("...", "ver", "rev");
        Assert.assertNotEquals(req1.hashCode(), req2.hashCode());

        req2 = new Requirement(null, "ver", "rev");
        Assert.assertNotEquals(req1.hashCode(), req2.hashCode());

        req2 = new Requirement("id", "...", "rev");
        Assert.assertNotEquals(req1.hashCode(), req2.hashCode());

        req2 = new Requirement("id", null, "rev");
        Assert.assertNotEquals(req1.hashCode(), req2.hashCode());

        req2 = new Requirement("id", "ver", "...");
        Assert.assertNotEquals(req1.hashCode(), req2.hashCode());

        req2 = new Requirement("id", "ver", null);
        Assert.assertNotEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    public void testEquals() {

        Requirement req1 = new Requirement("id", "ver", "rev");
        Requirement req2 = new Requirement("id", "ver", "rev");

        Assert.assertEquals(req1, req2);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("...", "ver", "rev");
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement(null, "ver", "rev");
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement(null, "ver", "rev");
        req2 = new Requirement(null, "ver", "rev");
        Assert.assertEquals(req1, req2);

        req1 = new Requirement(null, "ver", "rev");
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("id", "...", "rev");
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("id", null, "rev");
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement("id", null, "rev");
        req2 = new Requirement("id", null, "rev");
        Assert.assertEquals(req1, req2);

        req1 = new Requirement("id", null, "rev");
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("id", "ver", "...");
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("id", "ver", null);
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement("id", "ver", null);
        req2 = new Requirement("id", "ver", null);
        Assert.assertEquals(req1, req2);

        req1 = new Requirement("id", "ver", null);
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertNotEquals(req1, req2);

        req1 = new Requirement("id", "ver", null);
        req2 = new Requirement("id", "ver", null);
        Assert.assertEquals(req1, req2);

        Assert.assertNotEquals(null, req2);
        Assert.assertNotEquals(req1, null);

        req1 = new Requirement("id", "ver", "rev");
        req1.setIgnore(true);
        req1.setShortDescription("......");
        req2 = new SubRequirement("id", "ver", "rev", "dummy");
        Assert.assertEquals(req1, req2);

        Assert.assertEquals(req1, req1);

        Assert.assertNotEquals(req1, new Object());
    }

    @Test
    public void testCompare() {

        Requirement req1 = new Requirement("id", "ver", "rev");
        Requirement req2 = new Requirement("id", "ver", "rev");
        Assert.assertEquals(0, new Requirement().compare(req1, req2));

        req1 = new Requirement("id", "ver", "...");
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertTrue(new Requirement().compare(req1, req2) < 0);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("id", "ver", "...");
        Assert.assertTrue(new Requirement().compare(req1, req2) > 0);

        req1 = new Requirement("id", "...", "rev");
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertTrue(new Requirement().compare(req1, req2) < 0);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("id", "...", "rev");
        Assert.assertTrue(new Requirement().compare(req1, req2) > 0);

        req1 = new Requirement("...", "ver", "rev");
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertTrue(new Requirement().compare(req1, req2) < 0);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("...", "ver", "rev");
        Assert.assertTrue(new Requirement().compare(req1, req2) > 0);
    }

    @Test
    public void testCompareTo() {

        Requirement req1 = new Requirement("id", "ver", "rev");
        Requirement req2 = new Requirement("id", "ver", "rev");
        Assert.assertEquals(0, req1.compareTo(req2));

        req1 = new Requirement("id", "ver", "...");
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertTrue(req1.compareTo(req2) < 0);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("id", "ver", "...");
        Assert.assertTrue(req1.compareTo(req2) > 0);

        req1 = new Requirement("id", "...", "rev");
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertTrue(req1.compareTo(req2) < 0);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("id", "...", "rev");
        Assert.assertTrue(req1.compareTo(req2) > 0);

        req1 = new Requirement("...", "ver", "rev");
        req2 = new Requirement("id", "ver", "rev");
        Assert.assertTrue(req1.compareTo(req2) < 0);

        req1 = new Requirement("id", "ver", "rev");
        req2 = new Requirement("...", "ver", "rev");
        Assert.assertTrue(req1.compareTo(req2) > 0);
    }

    private static class SubRequirement extends Requirement {

        private static final long serialVersionUID = 1L;

        @Getter
        private String            dummyField;

        public SubRequirement(String id, String version, String revision, String dummyField) {
            super(id, version, revision);
            this.dummyField = dummyField;
        }

    }

}
