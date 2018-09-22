package net.paissad.tools.reqcoco.core.report;

import java.io.Serializable;
import java.util.Collection;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.api.model.Requirement;

/**
 * Represents the requirements coverage report for one and only one version.
 * 
 * @author paissad
 */
@Getter
@Setter
public class ReqReport implements Serializable {

    private static final long       serialVersionUID = 1L;

    @Expose
    private String                  version;

    @Expose
    private long                    codeDoneCount;

    @Expose
    private long                    codeUndoneCount;

    @Expose
    private float                   codeDoneRatio;

    @Expose
    private long                    testsDoneCount;

    @Expose
    private long                    testsUndoneCount;

    @Expose
    private float                   testsDoneRatio;

    @Expose
    private long                    ignoredRequirementsCount;

    @Expose
    private float                   ignoredRequirementsRatio;

    @Expose
    private long                    totalRequirements;

    @Expose
    private Collection<Requirement> requirements;
}
