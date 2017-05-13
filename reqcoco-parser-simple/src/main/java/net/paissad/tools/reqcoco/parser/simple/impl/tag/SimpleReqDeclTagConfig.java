package net.paissad.tools.reqcoco.parser.simple.impl.tag;

import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;

public class SimpleReqDeclTagConfig implements ReqDeclTagConfig {

	@Override
	public String getCompleteRegex() {
		return "(@Req\\s*\\(\\s*.*?\\))";
	}

	@Override
	public String getIdRegex() {
		return "@Req\\s*\\(.*id\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

	@Override
	public String getVersionRegex() {
		return "@Req\\s*\\(.*version\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

	@Override
	public String getRevisionRegex() {
		return "@Req\\s*\\(.*revision\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

	@Override
	public String getSummaryRegex() {
		return "@Req\\s*\\(.*summary\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

}
