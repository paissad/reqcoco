package net.paissad.tools.reqcoco.runner;

import org.junit.Assert;
import org.junit.Test;

import net.paissad.tools.reqcoco.generator.redmine.parser.RedmineReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.impl.parser.FileReqSourceParser;

public class ReqSourceTypeTest {

	@Test
	public void testGetParserFile() {
		Assert.assertEquals(FileReqSourceParser.class, ReqSourceType.FILE.getParser().getClass());
	}

	@Test
	public void testGetParserRedmine() {
		Assert.assertEquals(RedmineReqSourceParser.class, ReqSourceType.REDMINE.getParser().getClass());
	}

}
