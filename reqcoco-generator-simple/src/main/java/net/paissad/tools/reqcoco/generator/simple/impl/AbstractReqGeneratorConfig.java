package net.paissad.tools.reqcoco.generator.simple.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.generator.simple.api.ReqGeneratorConfig;
import net.paissad.tools.reqcoco.generator.simple.api.ReqTagConfig;
import net.paissad.tools.reqcoco.generator.simple.impl.tag.SimpleReqTagSourceConfig;
import net.paissad.tools.reqcoco.generator.simple.impl.tag.SimpleReqTagTestConfig;

@Getter
@Setter
public abstract class AbstractReqGeneratorConfig implements ReqGeneratorConfig {

	private ReqTagConfig		sourceCodeTagConfig;

	private ReqTagConfig		testsCodeTagConfig;

	private Collection<String>	ignoreList;

	private List<String>		fileIncludes;

	private List<String>		fileExcludes;

	private Map<String, Object>	extraOptions;

	public AbstractReqGeneratorConfig() {
		this.setSourceCodeTagConfig(new SimpleReqTagSourceConfig());
		this.setTestsCodeTagConfig(new SimpleReqTagTestConfig());
		this.setIgnoreList(new ArrayList<>());
		this.setFileIncludes(new ArrayList<>());
		this.setFileExcludes(new ArrayList<>());
		this.setExtraOptions(new HashMap<>());
	}

}
