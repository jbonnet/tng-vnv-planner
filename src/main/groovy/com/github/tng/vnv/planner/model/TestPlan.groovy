/*
 * Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 * ALL RIGHTS RESERVED.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Neither the name of the SONATA-NFV, 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 * nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * This work has been performed in the framework of the SONATA project,
 * funded by the European Commission under Grant number 671517 through
 * the Horizon 2020 and 5G-PPP programmes. The authors would like to
 * acknowledge the contributions of their colleagues of the SONATA
 * partner consortium (www.sonata-nfv.eu).
 *
 * This work has been performed in the framework of the 5GTANGO project,
 * funded by the European Commission under Grant number 761493 through
 * the Horizon 2020 and 5G-PPP programmes. The authors would like to
 * acknowledge the contributions of their colleagues of the 5GTANGO
 * partner consortium (www.5gtango.eu).
 */

package com.github.tng.vnv.planner.model


import groovy.transform.EqualsAndHashCode
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

@EqualsAndHashCode
class TestPlan {
    String uuid
    String packageId
    String nsdUuid
    String tdUuid
    String index
    String status
    NetworkServiceDescriptor nsd
    TestDescriptor testd


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TestPlan{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", packageId='").append(packageId).append('\'');
        sb.append(", nsdUuid='").append(nsdUuid).append('\'');
        sb.append(", tdUuid='").append(tdUuid).append('\'');
        sb.append(", index='").append(index).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", nsd.name=").append(nsd.name);
        sb.append(", testd.name=").append(testd.name);
        sb.append('}');
        return sb.toString();
    }
}

@EqualsAndHashCode
class TestPlanRequest {
    NetworkServiceDescriptor nsd
    TestDescriptor testd
    Boolean lastTest = false
    List<TestPlanCallback> testPlanCallbacks = [
            new TestPlanCallback(eventActor: 'Curator', url: '/test-plans/on-change/completed', status:TEST_PLAN_STATUS.COMPLETED),
            new TestPlanCallback(eventActor: 'Curator', url: '/test-plans/on-change'),
    ]
}

@EqualsAndHashCode
class TestPlanResponse {
    String id
    TestPlan testPlan
    String status
}

class TestPlanCallback {

    @ApiModelProperty(
            value = 'Event Actor',
            allowEmptyValue = false,
            example = 'Curator, Executor',
            required = true
    )
    @NotNull
    String eventActor

    @ApiModelProperty(
            value = 'Test Plan Status',
            allowEmptyValue = false,
            example = 'STARTING, COMPLETED, CANCELLING, CANCELLED, ERROR',
            required = true
    )
    @NotNull
    String status

    @ApiModelProperty(required = true)
    @NotNull
    String testPlanUuid

    @ApiModelProperty(
            value = 'Test Plan Repository URI',
            allowEmptyValue = false,
            example = 'tng-cat, catalog, or xx.xx',
            required = false
    )
    @NotNull
    String testPlanRepository

    @ApiModelProperty(required = true)
    @NotNull
    String testResultsUuid

    @ApiModelProperty(
            value = 'Test Results Repository URI',
            allowEmptyValue = false,
            example = 'tng-res, results, or xx.xx',
            required = false
    )
    @NotNull
    String testResultsRepository
}


enum TEST_PLAN_STATUS{
    STARTING('STARTING'), COMPLETED('COMPLETED'), CANCELLING('CANCELLING'), CANCELLED('CANCELLED'), ERROR('ERROR')
}