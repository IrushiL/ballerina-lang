module io.ballerina.language.server.core {
    exports org.ballerinalang.langserver;
    exports org.ballerinalang.langserver.util.references;
    exports org.ballerinalang.langserver.common.utils;
    exports org.ballerinalang.langserver.common.constants;
    exports org.ballerinalang.langserver.codeaction.providers;
    requires org.eclipse.lsp4j;
    requires io.ballerina.language.server.compiler;
    requires io.ballerina.language.server.commons;
    requires gson;
    requires org.apache.commons.lang3;
    requires org.eclipse.lsp4j.jsonrpc;
    requires io.ballerina.lang;
    requires io.ballerina.jvm;
    requires org.apache.commons.io;
//    requires io.ballerina.openapi.convertor;
    requires handlebars;
    requires io.ballerina.parser;
    requires jsr305;
    requires antlr4.runtime;
    requires toml4j;
//    requires io.ballerina.openapi.generator;
    requires swagger.parser.v3;
    requires io.ballerina.tools.api;
    requires swagger.models;
    requires swagger.parser;
    requires swagger.core;
    requires io.swagger.v3.core;
    requires io.swagger.v3.oas.models;
    requires swagger.parser.v2.converter;
}
