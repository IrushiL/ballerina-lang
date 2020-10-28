// Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

function testListMatchPatternNegative() returns string {
    any v1 = 0;
    match v1 {
        var a => {
            return "var a";
        }
        var [a, b] => { // unreachable pattern
            return "var [a, b]";
        }
    }

    any v2 = 0; // unreachable code
    match v2 {
        var a | var [a, b] => { // unreachable pattern, all match patterns should contain same set of variables
            return "var a | var [a, b]";
        }
    }

    int v3 = 2; // unreachable code
    match v3 {
        var [a, b] => { // pattern will not be matched
            return "var [a, b]";
        }
        [var [a], var [b]] => { // pattern will not be matched
            return "[var [a], var [b]]";
        }
    }

    [int, int] v5 = [1, 2];
    match v5 {
        _ | var [a, b] => { // unreachable pattern
            return "_ | var [a, b]";
        }
    }

    return "No match";
}

function testListOfMatchPatternsNegative() returns string {
    [int, int] v1 = [1, 2];
    match v1 {
        [var a, 2] | var [a, b] => { // match patterns should contain same set of variables
            return "match1";
        }
    }

    [int, int] v2 = [1, 2];
    match v2 {
        [var a, 2] | var [a, b] | var [a, b, c] => { // match patterns should contain same set of variables
                                                                 // pattern will not be matched
            return "match1";
        }
    }

    return "No match";
}

const X = 2;
const Y = 4;
function testSameMatchPatternsNegative() {
    [int, int] v = [1, 2];
    match v {
        var [a, b] | var [a, b] => { // unreachable pattern
        }
        var [a, c] => {
        }
        var [a, c] => { // unreachable pattern
        }
        var [a, b, c] if a is int => { // pattern will not be matched
        }
        var [a, b, c] if b is int => { // pattern will not be matched
        }
        var [a, b, c] if a is int => { // unreachable pattern, pattern will not be matched
        }
    }
}
