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

import java.lang.reflect.Parameter;

import static org.ballerinalang.cli.bbgen.utils.BBGenUtils.balType;

/**
 * Class for storing specific parameter details of a Java method used for Ballerina bridge code generation.
 */
class JParameter {

    private String type;
    private String shortTypeName;
    private String fieldName;
    private boolean notLast = true;

    JParameter(Parameter p) {

        Class paramType = p.getType();
        if (paramType.isArray()) {
            if (paramType.isPrimitive()) {
                // TODO: Get these values from the Ballerina Interop code.
                if (paramType == int.class) {
                    this.type = "[I";
                } else if (paramType == long.class) {
                    this.type = "[J";
                } else if (paramType == boolean.class) {
                    this.type = "[Z";
                } else if (paramType == byte.class) {
                    this.type = "[B";
                } else if (paramType == short.class) {
                    this.type = "[S";
                } else if (paramType == char.class) {
                    this.type = "[C";
                } else if (paramType == float.class) {
                    this.type = "[F";
                } else if (paramType == double.class) {
                    this.type = "[D";
                }
            } else {
                this.type = paramType.getName();
            }
        } else {
            this.type = paramType.getCanonicalName();
        }
        System.out.println(this.type);
        this.shortTypeName = balType(p.getType().getSimpleName());
        this.fieldName = p.getName();
    }

    void setLastParam() {

        this.notLast = false;
    }
}
