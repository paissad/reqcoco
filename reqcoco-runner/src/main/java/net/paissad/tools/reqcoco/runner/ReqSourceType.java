package net.paissad.tools.reqcoco.runner;

import lombok.Getter;
import net.paissad.tools.reqcoco.parser.docx.DocxReqSourceParser;
import net.paissad.tools.reqcoco.parser.github.GithubReqSourceParser;
import net.paissad.tools.reqcoco.parser.redmine.RedmineReqSourceParser;
import net.paissad.tools.reqcoco.parser.simple.api.ReqSourceParser;
import net.paissad.tools.reqcoco.parser.simple.impl.FileReqSourceParser;
import net.paissad.tools.reqcoco.parser.xlsx.XlsxReqSourceParser;

public enum ReqSourceType {

    FILE(new FileReqSourceParser()), DOCX(new DocxReqSourceParser()), XLSX(new XlsxReqSourceParser()), REDMINE(new RedmineReqSourceParser()), GITHUB(new GithubReqSourceParser());

    @Getter
    private final ReqSourceParser parser;

    private ReqSourceType(final ReqSourceParser reqSourceParser) {
        this.parser = reqSourceParser;
    }

}
