/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.cli.bbgen;

import java.io.PrintStream;

import org.ballerinalang.cli.bbgen.exceptions.BBGenException;
import org.ballerinalang.tool.BLauncherCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import static org.ballerinalang.cli.bbgen.utils.Constants.COMPONENT_IDENTIFIER;

/**
 * Class to implement "bbgen" command for Ballerina.
 * Ex: ballerina bbgen --jar <jar-file-path> [--output <path>]
 * ballerina bbgen --mvn=<groupId>:<artifactId>:<version> [--output <path>]
 * ballerina bbgen <-cn|--class-name>=<class-name>... [--output <path>]
 */
@CommandLine.Command(
        name = "bbgen",
        description = "A tool for generating Ballerina bindings to Java APIs.")
public class BBGenCommand implements BLauncherCmd {

    private static final Logger LOG = LoggerFactory.getLogger(BBGenCommand.class);
    private static final PrintStream outStream = System.out;

    private CommandLine parentCmdParser;

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"--jar"},
            description = "Path to the jar file from which the interop code is to be generated.")
    private String jarPath;

    @CommandLine.Option(names = {"--mvn"},
            description = "Maven dependency details for which the bridge code is to be generated."
    )
    private String mvnDependency;

    @CommandLine.Option(names = {"--output"},
            description = "Location for generated jBallerina bridge code."
    )
    private String outputPath;

    @CommandLine.Option(names = {"-cn", "--class-name"},
            description = "Comma-delimited FQNs of standard Java classes for which the bridge code is to be generated.")
    private String standardJavaClasses;

    @Override
    public void execute() {

        //Help flag check
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(getName());
            outStream.println(commandUsageInfo);
            return;
        }

        BridgeCodeGenerator bridgeCodeGenerator = new BridgeCodeGenerator(jarPath);
        try {
            bridgeCodeGenerator.generateBridgeCode();
        } catch (BBGenException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public String getName() {

        return COMPONENT_IDENTIFIER;
    }

    @Override
    public void printLongDesc(StringBuilder out) {

        out.append("Generates Ballerina bindings to Java APIs for Jars,").append(System.lineSeparator());
        out.append("Maven dependencies and standard Java classes.").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }

    @Override
    public void printUsage(StringBuilder out) {

        out.append("  ballerina " + COMPONENT_IDENTIFIER + " --jar /Users/mike/snakeyaml-1.25.jar\n");
        out.append("  ballerina " + COMPONENT_IDENTIFIER + " --mvn org.yaml:snakeyaml:1.25\n");
        out.append("  ballerina " + COMPONENT_IDENTIFIER + " --class-name java.util.Collection,java.util.HashSet,java.util.LinkedHashMap\n");
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {

        this.parentCmdParser = parentCmdParser;
    }
}


