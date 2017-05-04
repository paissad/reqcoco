package net.paissad.tools.reqcoco.generator.simple.impl.tag;

public class SimpleReqTagTestConfig extends AbstractReqCodeTagConfig {

	@Override
	public String getCompleteRegex() {
		return "(@ReqTestCode\\(\\s*.*?\\))";
	}
}
