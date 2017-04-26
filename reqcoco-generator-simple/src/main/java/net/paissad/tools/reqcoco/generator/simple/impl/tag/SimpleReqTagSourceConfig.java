package net.paissad.tools.reqcoco.generator.simple.impl.tag;

public class SimpleReqTagSourceConfig extends AbstractReqTagConfig {

	@Override
	public String getCompleteRegex() {
		return "(@ReqSourceCode\\(\\s*.*?\\))";
	}
}
