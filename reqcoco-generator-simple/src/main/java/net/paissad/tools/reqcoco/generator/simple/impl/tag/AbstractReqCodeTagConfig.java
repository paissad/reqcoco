package net.paissad.tools.reqcoco.generator.simple.impl.tag;

import net.paissad.tools.reqcoco.generator.simple.api.ReqCodeTagConfig;

/**
 * Default and simple implementation of {@link ReqCodeTagConfig}.
 * 
 * @author paissad
 */
public abstract class AbstractReqCodeTagConfig implements ReqCodeTagConfig {

	@Override
	public String getIdRegex() {
		return "@Req.*?Code\\(.*id\\s*=\\s*\"(.*?)\\\".*";
	}

	@Override
	public String getVersionRegex() {
		return "@Req.*?Code\\(.*version\\s*=\\s*\"(.*?)\\\".*";
	}

	@Override
	public String getRevisionRegex() {
		return "@Req.*?Code\\(.*revision\\s*=\\s*\"(.*?)\\\".*";
	}

	@Override
	public String getAuthorRegex() {
		return "@Req.*?Code\\(.*author\\s*=\\s*\"(.*?)\\\".*";
	}

	@Override
	public String getCommentRegex() {
		return "@Req.*?Code\\(.*comment\\s*=\\s*\"(.*?)\\\".*";
	}

}
