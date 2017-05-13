package net.paissad.tools.reqcoco.parser.simple.impl.tag;

import net.paissad.tools.reqcoco.parser.simple.api.ReqCodeTagConfig;

/**
 * Default and simple implementation of {@link ReqCodeTagConfig}.
 * 
 * @author paissad
 */
public abstract class AbstractReqCodeTagConfig implements ReqCodeTagConfig {

	@Override
	public String getIdRegex() {
		return "@Req.*?Code\\s*\\(.*id\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

	@Override
	public String getVersionRegex() {
		return "@Req.*?Code\\s*\\(.*version\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

	@Override
	public String getRevisionRegex() {
		return "@Req.*?Code\\s*\\(.*revision\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

	@Override
	public String getAuthorRegex() {
		return "@Req.*?Code\\s*\\(.*author\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

	@Override
	public String getCommentRegex() {
		return "@Req.*?Code\\s*\\(.*comment\\s*=\\s*[\"|«|](.*?)[\"|»].*";
	}

}
