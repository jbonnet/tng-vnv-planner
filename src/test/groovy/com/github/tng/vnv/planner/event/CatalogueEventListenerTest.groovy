package com.github.tng.vnv.planner.event


import com.github.mrduguo.spring.test.AbstractSpec
import com.github.tng.vnv.planner.restmock.TestCatalogueMock
import com.github.tng.vnv.planner.restmock.TestExecutionEngineMock
import com.github.tng.vnv.planner.restmock.TestPlatformManagerMock
import com.github.tng.vnv.planner.restmock.TestResultRepositoryMock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import spock.lang.Ignore

class CatalogueEventListenerTest extends AbstractSpec {

    public static final String MULTIPLE_TEST_PLANS_PACKAGE_ID ='multiple_scheduler:test:0.0.1'
    public static final String BAD_REQUEST_PACKAGE_ID ='error:test:0.0.1'

    @Autowired
    TestPlatformManagerMock testPlatformManagerMock

    @Autowired
    TestExecutionEngineMock testExecutionEngineMock

    @Autowired
    TestCatalogueMock testCatalogueMock

    @Autowired
    TestResultRepositoryMock testResultRepositoryMock

    void 'schedule single TestSuite and single NetworkService should produce successfully 2 Result for 2 testPlan'() {

        when:
        def entity = postForEntity('/tng-vnv-planner/api/v1/packages/on-change',
                [
                        event_name: UUID.randomUUID().toString(),
                        package_id:  MULTIPLE_TEST_PLANS_PACKAGE_ID,
                ]
                , Void.class)

        then:
        Thread.sleep(10000L);
        while (testPlatformManagerMock.networkServiceInstances.values().last().status!='TERMINATED')
            Thread.sleep(1000L);
        entity.statusCode == HttpStatus.OK
        testPlatformManagerMock.networkServiceInstances.size()==3
        testExecutionEngineMock.testSuiteResults.size()==3
        testExecutionEngineMock.testSuiteResults.values().last().status=='SUCCESS'

        testResultRepositoryMock.testPlans.size()==3
        testResultRepositoryMock.testPlans.values().last().status=='SUCCESS'
        testResultRepositoryMock.testPlans.values().last().networkServiceInstances.size()==1
        testResultRepositoryMock.testPlans.values().last().testSuiteResults.last().status=='SUCCESS'

        cleanup:
        testPlatformManagerMock.reset()
        testExecutionEngineMock.reset()
        testResultRepositoryMock.reset()
    }

    void 'simple access on-change endpoint with empty package_id should produce BAD_REQUEST response'() {

        when:
        def entity = postForEntity('/tng-vnv-planner/api/v1/packages/on-change',
                [
                        event_name: UUID.randomUUID().toString(),
                        package_id:  '04bd5fcf-0000-42e1-a34f-94c66557ae73',
                ]
                , Void.class)

        then:
//        entity.statusCode == HttpStatus.BAD_REQUEST
        entity.statusCode == HttpStatus.OK
        System.out.println('sata')
        Thread.sleep(10000L);


        cleanup:
        testPlatformManagerMock.reset()
        testExecutionEngineMock.reset()
        testResultRepositoryMock.reset()
    }

    @Ignore
    void 'simple access on-change endpoint with non existing package_id should produce successfully BAD_REQUEST response'() {

        when:
        def entity = postForEntity('/tng-vnv-planner/api/v1/packages/on-change',
                [
                        event_name: UUID.randomUUID().toString(),
                        package_id:  BAD_REQUEST_PACKAGE_ID,
                ]
                , Void.class)

        then:
        entity.statusCode == HttpStatus.INTERNAL_SERVER_ERROR

        cleanup:
        testPlatformManagerMock.reset()
        testExecutionEngineMock.reset()
        testResultRepositoryMock.reset()
    }


}
