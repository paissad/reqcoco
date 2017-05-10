package net.paissad.tools.reqcoco.generator.xlsx.parser;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.generator.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.generator.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqSourceParserException;

public class XlsxReqSourceParser implements ReqSourceParser {

	@Override
	public Collection<Requirement> parse(URI uri, ReqDeclTagConfig declTagConfig, Map<String, Object> options) throws ReqSourceParserException {
		// TODO Auto-generated method stub
		return Collections.emptySet();
	}

}
