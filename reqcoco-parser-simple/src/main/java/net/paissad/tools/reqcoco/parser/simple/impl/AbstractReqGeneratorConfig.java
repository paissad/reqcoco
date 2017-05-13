package net.paissad.tools.reqcoco.parser.simple.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.parser.simple.api.ReqCodeTagConfig;
import net.paissad.tools.reqcoco.parser.simple.api.ReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.api.ReqGeneratorConfig;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqDeclTagConfig;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqTagSourceConfig;
import net.paissad.tools.reqcoco.parser.simple.impl.tag.SimpleReqTagTestConfig;

@Getter
@Setter
public abstract class AbstractReqGeneratorConfig implements ReqGeneratorConfig {

	private ReqDeclTagConfig	declTagConfig;

	private ReqCodeTagConfig	sourceCodeTagConfig;

	private ReqCodeTagConfig	testsCodeTagConfig;

	private Collection<String>	ignoreList;

	private List<String>		fileIncludes;

	private List<String>		fileExcludes;

	private Map<String, Object>	extraOptions;

	public AbstractReqGeneratorConfig() {
		this.setDeclTagConfig(new SimpleReqDeclTagConfig());
		this.setSourceCodeTagConfig(new SimpleReqTagSourceConfig());
		this.setTestsCodeTagConfig(new SimpleReqTagTestConfig());
		this.setIgnoreList(new ArrayList<>());
		this.setFileIncludes(new ArrayList<>());
		this.setFileExcludes(new ArrayList<>());
		this.setExtraOptions(new HashMap<>());
	}

}
