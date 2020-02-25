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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.balType;
import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.isStaticMethod;
import static org.ballerinalang.cli.bbgen.utils.Constants.METHOD_INTEROP_TYPE;

/**
 * Class for storing details pertaining to a specific Java method used for Ballerina bridge code generation.
 */
public class JMethod {

    public Boolean params = false;
    public String methodName;

    private Boolean isStatic;
    private Boolean isInstance;
    private Boolean noParams = true;
    private Boolean noReturn = true;
    private Boolean hasReturn = false;
    private Boolean exceptionTypes = false;

    private String returnType;
    private String interopType;
    private String javaMethodName;

    private List<JParameter> parameters = new ArrayList<>();

    JMethod(Method m) {

        this.javaMethodName = m.getName();
        this.methodName = m.getName();

        // TODO: Get the keywords in Ballerina and make sure they are not used for method names.
        if (this.methodName.equals("is")) {
            this.methodName = "isFunc";
        }

        if (!m.getReturnType().equals(Void.TYPE)) {
            this.returnType = balType(m.getReturnType().getSimpleName());
            this.hasReturn = true;
            this.noReturn = false;
        }
        this.isInstance = !isStaticMethod(m);
        this.isStatic = isStaticMethod(m);
        for (Parameter param : m.getParameters()) {
            this.parameters.add(new JParameter(param));
        }
        this.exceptionTypes = true;
        if (!this.parameters.isEmpty()) {
            JParameter lastParam = this.parameters.get(this.parameters.size() - 1);
            lastParam.setLastParam();
        } else {
            this.noParams = false;
        }
        this.interopType = METHOD_INTEROP_TYPE;
    }
}
