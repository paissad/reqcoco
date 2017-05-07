package net.paissad.tools.reqcoco.generator.redmine.parser;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.generator.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.generator.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.exception.ReqSourceParserException;

public class RedmineReqSourceParser implements ReqSourceParser {

	@Override
	public Collection<Requirement> parse(final URI uri, final ReqDeclTagConfig declTagConfig, final Map<String, Object> options)
	        throws ReqSourceParserException {

		return null;
	}

}
