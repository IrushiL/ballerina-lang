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

package org.ballerinalang.cli.bbgen.utils;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import org.ballerinalang.cli.bbgen.components.JClass;
import org.ballerinalang.cli.bbgen.components.JMethod;
import org.ballerinalang.cli.bbgen.exceptions.BBGenException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.ballerinalang.cli.bbgen.utils.Constants.MUSTACHE_FILE_EXTENSION;
import static org.ballerinalang.cli.bbgen.utils.Constants.TEMPLATES_DIR_PATH_KEY;

/**
 * Class containing the util methods of the BBGen tool.
 */
public class BBGenUtils {

    private static final PrintStream errStream = System.err;

    public static void writeOutputFile(Object object, String templateDir, String templateName, String outPath)
            throws BBGenException {

        PrintWriter writer = null;
        try {
            Template template = compileTemplate(templateDir, templateName);
            Context context = Context.newBuilder(object).resolver(
                    MapValueResolver.INSTANCE,
                    JavaBeanValueResolver.INSTANCE,
                    FieldValueResolver.INSTANCE).build();
            writer = new PrintWriter(outPath, UTF_8.name());
            writer.println(template.apply(context));
        } catch (IOException e) {
            throw new BBGenException("IO error while writing output to Ballerina file. " + e.getMessage(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static Template compileTemplate(String defaultTemplateDir, String templateName)
            throws BBGenException {

        String templatesDirPath = System.getProperty(TEMPLATES_DIR_PATH_KEY, defaultTemplateDir);
        ClassPathTemplateLoader cpTemplateLoader = new ClassPathTemplateLoader((templatesDirPath));
        FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(templatesDirPath);
        cpTemplateLoader.setSuffix(MUSTACHE_FILE_EXTENSION);
        fileTemplateLoader.setSuffix(MUSTACHE_FILE_EXTENSION);
        Handlebars handlebars = new Handlebars().with(cpTemplateLoader, fileTemplateLoader);
        try {
            return handlebars.compile(templateName);
        } catch (FileNotFoundException e) {
            throw new BBGenException("Code generation template file does not exist. " + e.getMessage(), e);
        } catch (IOException e) {
            throw new BBGenException("IO error while compiling the template file. " + e.getMessage(), e);
        }
    }

    public static void createDirectory(String path) throws BBGenException {

        File directory = new File(path);
        if (!directory.exists()) {
            try {
                final boolean mkdirResult = directory.mkdir();
                if (!mkdirResult) {
                    errStream.println("directory " + path + " could not be created");
                }
            } catch (SecurityException e) {
                throw new BBGenException("Unable to create the directory: " + path, e);
            }
        }
    }

    public static List<String> getClassNamesInJar(String jarPath) throws IOException {

        JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarPath));
        JarEntry jarEntry;
        ArrayList classes = new ArrayList();
        try {
            while (true) {
                jarEntry = jarInputStream.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if ((jarEntry.getName().endsWith(".class"))) {
                    String className = jarEntry.getName().replace("/", ".");
                    classes.add(className.substring(0, className.length() - ".class".length()));
                }
            }
            jarInputStream.close();
        } catch (IOException e) {
            throw new IOException("error while accessing the jar: ", e);
        } finally {
            jarInputStream.close();
        }
        return classes;
    }

    public static boolean isPublicField(Field field) {

        int modifers = field.getModifiers();
        return Modifier.isPublic(modifers);
    }

    public static boolean isPublicMethod(Method method) {

        int modifers = method.getModifiers();
        return Modifier.isPublic(modifers);
    }

    public static boolean isPublicClass(Class javaClass) {

        int modifers = javaClass.getModifiers();
        return Modifier.isPublic(modifers);
    }

    public static boolean isStaticField(Field field) {

        int modifers = field.getModifiers();
        return Modifier.isStatic(modifers);
    }

    public static boolean isStaticMethod(Method method) {

        int modifers = method.getModifiers();
        return Modifier.isStatic(modifers);
    }

    public static boolean isFinalField(Field field) {

        int modifers = field.getModifiers();
        return Modifier.isFinal(modifers);
    }

    public static Set<JClass> getClasses(URLClassLoader classLoader, List<String> classNames) {

        Set<JClass> classList = new HashSet<>();
        for (String c : classNames) {
            try {
                Class classInstance = classLoader.loadClass(c);
                if (isPublicClass(classInstance)) {
                    JClass jClass = new JClass(classInstance);
                    classList.add(jClass);
                }
            } catch (ClassNotFoundException e) {
                errStream.println(e);
            }
        }
        return classList;
    }

    public static String getModuleName(String jarPath) {

        String name = null;
        if (jarPath != null) {
            Path jarName = Paths.get(jarPath).getFileName();
            if (jarName != null) {
                name = jarName.toString().substring(0, jarName.toString()
                        .lastIndexOf('.'));
                name = name.replace('-', '_');
            }
        }
        return name;
    }

    public static void handleOverloadedMethods(List<JMethod> methodList) {

        Map<String, Integer> methodNames = new HashMap<>();
        for (JMethod method : methodList) {
            String mName = method.methodName;
            if (methodNames.containsKey(mName)) {
                methodNames.replace(mName, methodNames.get(mName) + 1);
            } else {
                methodNames.put(mName, 1);
            }
        }
        for (Map.Entry<String, Integer> entry : methodNames.entrySet()) {
            if (entry.getValue() > 1) {
                int i = 1;
                for (JMethod jMethod : methodList) {
                    if (jMethod.methodName.equals(entry.getKey())) {
                        jMethod.methodName = jMethod.methodName + i;
                        jMethod.params = true;
                        i++;
                    }
                }
            }
        }
    }

    // TODO: Map these with the Ballerina types and primitives.
    public static String balType(String type) {

        switch (type) {
            case "int":
                return "int";
            case "float":
                return "float";
            case "boolean":
                return "boolean";
            case "byte":
                return "byte";
            case "short":
                return "int";
            case "char":
                return "int";
            case "double":
                return "float";
            case "long":
                return "int";
            default:
                return "handle";
        }
    }
}
