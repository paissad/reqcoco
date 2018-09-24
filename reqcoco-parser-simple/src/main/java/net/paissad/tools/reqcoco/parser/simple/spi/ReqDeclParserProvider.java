package net.paissad.tools.reqcoco.parser.simple.spi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public class ReqDeclParserProvider {

    private static ReqDeclParserProvider instance;

    private ServiceLoader<ReqDeclParser> loader;

    private ReqDeclParserProvider() {
        this.loader = ServiceLoader.load(ReqDeclParser.class);
    }

    public static synchronized ReqDeclParserProvider getInstance() {
        if (instance == null) {
            instance = new ReqDeclParserProvider();
        }
        return instance;
    }

    public Collection<ReqDeclParser> getRegisteredParsers() {

        final Iterator<ReqDeclParser> iterator = this.loader.iterator();

        final Set<ReqDeclParser> parsers = new HashSet<>();

        while (iterator.hasNext()) {
            parsers.add(iterator.next());
        }

        return parsers;
    }

    public ReqDeclParser getParserHavingIdentifier(final String identitfier) {

        final List<ReqDeclParser> matchedParsers = getRegisteredParsers().stream().filter(p -> identitfier.equals(p.getIdentitier())).distinct().collect(Collectors.toList());

        return matchedParsers.isEmpty() ? null : matchedParsers.get(0);
    }

    public ReqDeclParser getParserForFileExtension(final String fileExtension) {

        final Collection<ReqDeclParser> registeredParsers = getRegisteredParsers();

        ReqDeclParser defaultParser = null;

        for (final ReqDeclParser reqDeclParser : registeredParsers) {

            if (reqDeclParser.getRegisteredFileExtensions() != null) {

                if (reqDeclParser.getRegisteredFileExtensions().contains(ReqDeclParser.ALL_EXTENSIONS)) {
                    defaultParser = reqDeclParser;
                }

                if (fileExtension != null && reqDeclParser.getRegisteredFileExtensions().stream().anyMatch(ext -> ext.equalsIgnoreCase(fileExtension))) {
                    return reqDeclParser;
                }
            }
        }

        return defaultParser;
    }
}
