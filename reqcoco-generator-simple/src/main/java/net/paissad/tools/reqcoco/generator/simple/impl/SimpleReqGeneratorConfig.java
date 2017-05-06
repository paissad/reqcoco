package net.paissad.tools.reqcoco.generator.simple.impl;

import java.net.URI;
import java.nio.file.Path;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.generator.simple.api.ReqSourceParser;

@Getter
@Setter
public class SimpleReqGeneratorConfig extends AbstractReqGeneratorConfig {

	private URI				sourceRequirements;

	private ReqSourceParser	sourceParser;

	private Path			sourceCodePath;

	private Path			testsCodePath;

	private Path			coverageOutput;

}
