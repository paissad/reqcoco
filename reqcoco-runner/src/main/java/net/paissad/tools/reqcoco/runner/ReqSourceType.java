package net.paissad.tools.reqcoco.runner;

import lombok.Getter;
import net.paissad.tools.reqcoco.generator.docx.parser.DocxReqSourceParser;
import net.paissad.tools.reqcoco.generator.redmine.parser.RedmineReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.generator.simple.impl.parser.FileReqSourceParser;

public enum ReqSourceType {

	FILE(new FileReqSourceParser()), DOCX(new DocxReqSourceParser()), REDMINE(new RedmineReqSourceParser());

	@Getter
	private final ReqSourceParser parser;

	private ReqSourceType(final ReqSourceParser reqSourceParser) {
		this.parser = reqSourceParser;
	}

}
