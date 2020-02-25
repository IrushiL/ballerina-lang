/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.cli.bbgen;

import org.ballerinalang.cli.bbgen.components.JClass;
import org.ballerinalang.cli.bbgen.exceptions.BBGenException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.createDirectory;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.getClassNamesInJar;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.getModuleName;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.isPublicClass;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.writeOutputFile;
import static org.ballerinalang.cli.bbgen.utils.Constants.BAL_FILE_EXTENSION;
import static org.ballerinalang.cli.bbgen.utils.Constants.BBGEN_CLASS_TEMPLATE_NAME;
import static org.ballerinalang.cli.bbgen.utils.Constants.DEFAULT_TEMPLATE_DIR;
import static org.ballerinalang.cli.bbgen.utils.Constants.USER_DIR;

/**
 * Class for generating Ballerina bridge code.
 */
public class BridgeCodeGenerator {

    private static final PrintStream errStream = System.err;
    private static Path userDir = Paths.get(System.getProperty(USER_DIR));
    private String jarPathString;
    private Boolean standardJava = false;

    BridgeCodeGenerator(String jarPath) {
        this.jarPathString =jarPath;
    }

    public void generateBridgeCode() throws BBGenException {

        if (!standardJava) {
            URLClassLoader classLoader = null;
            Path jarPath = FileSystems.getDefault().getPath(jarPathString);
            try {
                URL[] url = {jarPath.toFile().toURI().toURL()};
                classLoader = new URLClassLoader(url);
            } catch (MalformedURLException e) {
                errStream.println(e);
            }
            String moduleName = getModuleName(jarPathString);
            Path modulePath = Paths.get(userDir.toString(), moduleName);
            createDirectory(modulePath.toString());
            List<String> classes = null;
            try {
                classes = getClassNamesInJar(jarPathString);
                if (!classes.isEmpty()) {
                    for (String c : classes) {
                        try {
                            if (classLoader != null) {
                                Class classInstance = classLoader.loadClass(c);
                                if (classInstance != null && isPublicClass(classInstance) && !classInstance.isEnum()
                                        && !classInstance.isInterface()) {
                                    JClass jClass = new JClass(classInstance);
                                    String outputFile = Paths.get(modulePath.toString(), jClass.packageName).toString();
                                    String filePath = Paths.get(outputFile,jClass.shortClassName + BAL_FILE_EXTENSION)
                                            .toString();
                                    createDirectory(outputFile);
                                    writeOutputFile(jClass, DEFAULT_TEMPLATE_DIR, BBGEN_CLASS_TEMPLATE_NAME,
                                            filePath);
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            errStream.println("Class not found: " + c);
                        } catch (BBGenException e) {
                            throw new BBGenException("Error while generating Ballerina bridge code: " + e);
                        }
                    }
                }
            } catch (IOException e) {
                errStream.println(e);
            } finally {
                try {
                    assert classLoader != null;
                    classLoader.close();
                } catch (IOException e) {
                    errStream.println(e);
                }
            }
        }
    }
}
