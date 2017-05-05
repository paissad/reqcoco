package net.paissad.tools.reqcoco.generator.simple.impl.tag;

public class SimpleReqTagSourceConfig extends AbstractReqCodeTagConfig {

	@Override
	public String getCompleteRegex() {
		return "(@ReqSourceCode\\s*\\(\\s*.*?\\))";
	}
}
