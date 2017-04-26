package net.paissad.tools.reqcoco.generator.simple.impl.tag;

public class SimpleReqTagTestConfig extends AbstractReqTagConfig {

	@Override
	public String getCompleteRegex() {
		return "(@ReqTestCode\\(\\s*.*?\\))";
	}
}
