package net.paissad.tools.reqcoco.parser.simple.impl;

import java.net.URI;
import java.nio.file.Path;

import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.parser.simple.spi.ReqDeclParser;

@Getter
@Setter
public class SimpleReqGeneratorConfig extends AbstractReqGeneratorConfig {

	private URI				sourceRequirements;

	private ReqDeclParser	sourceParser;

	private Path			sourceCodePath;

	private Path			testsCodePath;

	private Path			coverageOutput;

}
