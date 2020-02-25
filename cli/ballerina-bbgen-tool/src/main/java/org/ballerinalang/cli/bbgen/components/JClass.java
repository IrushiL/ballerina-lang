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

package org.ballerinalang.cli.bbgen.components;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.handleOverloadedMethods;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.isFinalField;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.isPublicField;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.isPublicMethod;

/**
 * Class for storing details pertaining to a specific Java class used for Ballerina bridge code generation.
 */
public class JClass {

    public String shortClassName;
    public String packageName;

    private String className;
    private String prefix;
    private Boolean singleConstructor = false;
    private List<JConstructor> initFunctionList = new ArrayList<>();
    private List<JConstructor> constructorList = new ArrayList<>();
    private List<JMethod> methodList = new ArrayList<>();
    private List<JField> fieldList = new ArrayList<>();

    private static final PrintStream errStream = System.err;

    public JClass(Class c) {

        this.className = c.getName();
        this.prefix = this.className.replace(".", "_").replace("$", "_");
        this.shortClassName = c.getSimpleName();
        this.packageName = c.getPackage().getName();
        populateConstructors(c.getConstructors());
        if (this.constructorList.size() == 1) {
            this.singleConstructor = true;
        } else {
            populateInitFunctions();
        }
        populateMethods(c.getDeclaredMethods());
        handleOverloadedMethods(methodList);
        populateFields(c.getFields());
    }

    private void populateConstructors(Constructor[] constructors) {
        int i = 1;
        for (Constructor constructor : constructors) {
            JConstructor jConstructor = new JConstructor(constructor);
            jConstructor.setConstructorName("new" + this.shortClassName + i);
            this.constructorList.add(jConstructor);
            i++;
        }
    }

    private void populateInitFunctions() {
        int j = 0;
        for (JConstructor constructor : this.constructorList) {
            JConstructor newCons = null;
            try {
                newCons = (JConstructor) constructor.clone();
            } catch (CloneNotSupportedException e) {
                errStream.println(e);
            }
            if (newCons != null) {
                newCons.setExternalFunctionName(constructor.constructorName);
                newCons.setConstructorName("" + j);
                this.initFunctionList.add(newCons);
            }
            j++;
        }
    }

    private void populateMethods(Method[] declaredMethods) {
        for (Method method : declaredMethods) {
            if (isPublicMethod(method)) {
                this.methodList.add(new JMethod(method));
            }
        }
    }

    private void populateFields(Field[] fields) {
        for (Field field : fields) {
            this.fieldList.add(new JField(field, "access"));
            if (!isFinalField(field) && isPublicField(field)) {
                this.fieldList.add(new JField(field, "mutate"));
            }
        }
    }
}
