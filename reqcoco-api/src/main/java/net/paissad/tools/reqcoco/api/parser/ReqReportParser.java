package net.paissad.tools.reqcoco.api.parser;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

import net.paissad.tools.reqcoco.api.exception.ReqReportParserException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.api.model.Version;

public interface ReqReportParser {

	void setSource(final URI uri, final Map<String, Object> options) throws IOException;

	Requirements getRequirements() throws ReqReportParserException;

	Collection<Requirement> getRequirements(final Version version) throws ReqReportParserException;

}
