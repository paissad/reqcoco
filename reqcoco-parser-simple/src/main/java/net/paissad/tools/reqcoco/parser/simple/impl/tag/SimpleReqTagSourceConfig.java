package net.paissad.tools.reqcoco.parser.simple.impl.tag;

public class SimpleReqTagSourceConfig extends AbstractReqCodeTagConfig {

	@Override
	public String getCompleteRegex() {
		return "(@ReqSourceCode\\s*\\(\\s*.*?\\))";
	}
}
