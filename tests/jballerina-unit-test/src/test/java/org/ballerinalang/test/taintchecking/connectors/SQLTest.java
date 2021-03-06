/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.taintchecking.connectors;

import org.ballerinalang.test.util.BAssertUtil;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test SQL client connector for taint checking operations.
 *
 * @since 0.965.0
 */
@Test(enabled = false)
public class SQLTest {

    //TODO Table remove - Fix
    @Test (enabled = false)
    public void testSelectWithUntaintedQuery() {
        CompileResult result = BCompileUtil
                .compile("test-src/taintchecking/connectors/sql-select-untainted-query.bal");
        Assert.assertEquals(result.getDiagnostics().length, 3);
    }

    @Test (enabled = false)
    public void testSelectWithTaintedQueryNegative() {
        CompileResult result = BCompileUtil
                .compile("test-src/taintchecking/connectors/sql-select-tainted-query-negative.bal");
        Assert.assertEquals(result.getDiagnostics().length, 4);
        BAssertUtil.validateError(result, 0, "tainted value passed to untainted parameter 'args'", 4, 40);
    }


    @Test (enabled = false)
    public void testSelectWithUntaintedQueryProducingTaintedReturn() {
        CompileResult result = BCompileUtil
                .compile("test-src/taintchecking/connectors/sql-select-untainted-query-tainted-return.bal");
        Assert.assertEquals(result.getDiagnostics().length, 2);
    }

    @Test (enabled = false)
    public void testSelectWithUntaintedQueryProducingTaintedReturnNegative() {
        CompileResult result = BCompileUtil
                .compile("test-src/taintchecking/connectors/sql-select-untainted-query-tainted-return-negative.bal");
        Assert.assertEquals(result.getDiagnostics().length, 4);
        BAssertUtil.validateError(result, 2, "tainted value passed to untainted parameter 'anyValue'", 25, 26);
    }
}
