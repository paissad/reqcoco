package net.paissad.tools.reqcoco.runner;

import lombok.Getter;
import net.paissad.tools.reqcoco.parser.docx.DocxReqDeclParser;
import net.paissad.tools.reqcoco.parser.github.GithubReqDeclParser;
import net.paissad.tools.reqcoco.parser.redmine.RedmineReqDeclParser;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclParser;
import net.paissad.tools.reqcoco.parser.simple.impl.FileReqDeclParser;
import net.paissad.tools.reqcoco.parser.xlsx.XlsxReqDeclParser;

public enum ReqSourceType {

    FILE(new FileReqDeclParser()),
    DOCX(new DocxReqDeclParser()),
    XLSX(new XlsxReqDeclParser()),
    REDMINE(new RedmineReqDeclParser()), 
    GITHUB(new GithubReqDeclParser());

    @Getter
    private final ReqDeclParser parser;

    private ReqSourceType(final ReqDeclParser reqSourceParser) {
        this.parser = reqSourceParser;
    }

}
