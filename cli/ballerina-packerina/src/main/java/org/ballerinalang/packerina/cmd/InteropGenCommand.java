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

package org.ballerinalang.packerina.cmd;

import org.ballerinalang.tool.BLauncherCmd;
import picocli.CommandLine;

import java.io.PrintStream;
import java.util.List;

import static org.ballerinalang.packerina.cmd.Constants.INTEROP_GEN_COMMAND;

/**
 * This class represents the "ballerina interop-gen" command.
 *
 * @since 1.2.0
 */
@CommandLine.Command(name = INTEROP_GEN_COMMAND, description = "Ballerina interop-gen - Generates Ballerina interop " +
        "functions for the Java class specified.")
public class InteropGenCommand implements BLauncherCmd {

    private final PrintStream outStream;
    private final PrintStream errStream;

    public InteropGenCommand() {

        this.outStream = System.out;
        this.errStream = System.err;
    }

    @CommandLine.Parameters
    private List<String> argList;

    @CommandLine.Option(names = {"--help", "-h"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"--class-name", "-cn"},
            description = "The fully qualified name of the class to be used in the interop generation.")
    private String className;

    @CommandLine.Option(names = {"--method", "-m"},
            description = "The list of comma-delimited methods to be used for the interop generation.")
    private String[] methods;

    @CommandLine.Option(names = {"--field", "-f"},
            description = "The list of comma-delimited fields to be used for the interop generation.")
    private String[] fields;

    @CommandLine.Option(names = {"--constructor", "-c"},
            description = "The option to generate interop code for constructors.")
    private boolean constructors;

    @CommandLine.Option(names = {"--alias"},
            description = "An alias to represent the fully qualified name of a class in interop functions.")
    private String alias;

    @CommandLine.Option(names = {"--jar"},
            description = "The path to the .jar file from which the interop functions are to be generated.")
    private String jarPath;

    private static final String interopGenCmd = "ballerina interop-gen (<module-name> | <bal-file-path>) " +
            "<-cn|--class-name>=<class-name> [<-m|--method>=<method-name>...] [<-f|--field>=<field-name>...] " +
            "[-c|--constructor] [--alias=<alias-name>] [--jar=<jar-path>]";

    @Override
    public void execute() {

        // Prints the help message, if the help flag is given.
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(INTEROP_GEN_COMMAND);
            this.errStream.println(commandUsageInfo);
            return;
        }
    }

    @Override
    public String getName() {

        return INTEROP_GEN_COMMAND;
    }

    @Override
    public void printLongDesc(StringBuilder out) {

        out.append("Generates jBallerina interop functions for a specified Java class residing inside standard " +
                "Java libraries, libraries used by the Ballerina SDK (bre/lib), dependencies specified in a " +
                "project’s Ballerina.toml (if the tool is run inside a project) or from a specified .jar file.");
    }

    @Override
    public void printUsage(StringBuilder out) {

        out.append(" " + interopGenCmd + "\n");
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {

    }
}
