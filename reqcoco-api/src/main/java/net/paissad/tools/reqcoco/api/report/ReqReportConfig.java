package net.paissad.tools.reqcoco.api.report;

import java.util.Collection;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ReqReportConfig {

    @Expose
    private String             title                    = "Requirements Coverage";

    @Expose
    private String             codeCoverageDiagramName  = "Source Code Coverage";

    @Expose
    private String             testsCoverageDiagramName = "Tests Code Coverage";

    @Expose
    private String             requirementsTableLegend  = "Table of requirements";

    @Expose
    @Setter
    private Collection<String> filteredVersions;
}
