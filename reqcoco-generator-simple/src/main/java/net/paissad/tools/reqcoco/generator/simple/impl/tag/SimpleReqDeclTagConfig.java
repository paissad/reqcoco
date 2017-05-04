package net.paissad.tools.reqcoco.generator.simple.impl.tag;

import net.paissad.tools.reqcoco.generator.simple.api.ReqDeclTagConfig;

public class SimpleReqDeclTagConfig implements ReqDeclTagConfig {

	@Override
	public String getCompleteRegex() {
		return "(@Req\\(\\s*.*?\\))";
	}

	@Override
	public String getIdRegex() {
		return "@Req\\(.*id\\s*=\\s*\"(.*?)\\\".*";
	}

	@Override
	public String getVersionRegex() {
		return "@Req\\(.*version\\s*=\\s*\"(.*?)\\\".*";
	}

	@Override
	public String getRevisionRegex() {
		return "@Req\\(.*revision\\s*=\\s*\"(.*?)\\\".*";
	}

	@Override
	public String getSummaryRegex() {
		return "@Req\\(.*summary\\s*=\\s*\"(.*?)\\\".*";
	}

}
