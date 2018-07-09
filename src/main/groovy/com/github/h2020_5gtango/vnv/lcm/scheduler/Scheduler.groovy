package com.github.h2020_5gtango.vnv.lcm.scheduler

import com.github.h2020_5gtango.vnv.lcm.restclient.TestCatalogue
import com.github.h2020_5gtango.vnv.lcm.model.NetworkService
import com.github.h2020_5gtango.vnv.lcm.model.TestSuite
import com.github.h2020_5gtango.vnv.lcm.workflow.WorkflowManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Scheduler {

    @Autowired
    TestCatalogue testCatalogue

    @Autowired
    WorkflowManager workflowManager

    void scheduleTests(String packageId) {
        Map<NetworkService, List<TestSuite>> nsAndTestsMapping = discoverNssAndTestsToRun(packageId)
        nsAndTestsMapping.each {ns,testSuites->
            workflowManager.execute(ns,testSuites)
        }
    }

    Map discoverNssAndTestsToRun(String packageId) {
        def packageMetadata = testCatalogue.loadPackageMetadata(packageId)
        Map nsAndTestsMapping = [:]

        packageMetadata.networkServices.each { ns ->
            testCatalogue.findTestsApplicableToNs(ns).each { testSuite ->
                addNsTestToMap(nsAndTestsMapping, ns, testSuite)
            }
        }

        packageMetadata.testSuites.each { testSuite ->
            testCatalogue.findNsApplicableToTest(testSuite).each { ns ->
                addNsTestToMap(nsAndTestsMapping, ns, testSuite)
            }
        }

        packageMetadata.testd?.testExecution?.each { testTag ->
            if(testTag.testTag=='ns_under_test'){
                def nsSpec=testTag.tagId.split(':')

                addNsTestToMap(nsAndTestsMapping,
                        testCatalogue.findNsBySpec(nsSpec[0],nsSpec[1],nsSpec[2]),
                        new TestSuite(
                                packageId: packageMetadata.packageId,
                                testUuid: packageMetadata.uuid,
                                vendor: packageMetadata.testd.vendor,
                                name: packageMetadata.testd.name,
                                version: packageMetadata.testd.version,
                        ))
            }
        }

        nsAndTestsMapping
    }

    void addNsTestToMap(Map nsAndTestsMapping, NetworkService ns, TestSuite testSuite) {
        List nsTests = nsAndTestsMapping.get(ns)
        if (!nsTests) {
            nsTests = []
            nsAndTestsMapping.put(ns, nsTests)
        }
        if (!nsTests.contains(testSuite)) {
            nsTests << testSuite
        }
    }
}
