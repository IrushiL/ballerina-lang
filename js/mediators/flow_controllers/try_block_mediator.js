/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var Mediators = (function (mediators) {

    var flowControllers = mediators.flowControllers = {};

    //Define manipulator mediators
    var tryBlockMediator = {
        id: "TryBlockMediator",
        name: "TryBlock Mediator",
        icon: "images/TryBlockMediator.gif",
        parameters: []
    };

    // Add defined mediators to manipulators
    // Mediator id should be exactly match to name defining here.(Eg : "LogMediator")
    flowControllers.TryBlockMediator = tryBlockMediator;

    mediators.flowControllers = flowControllers;

    return mediators;

}(Mediators || {}));
