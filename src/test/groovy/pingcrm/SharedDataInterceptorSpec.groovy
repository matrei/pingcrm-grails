package pingcrm

import spock.lang.Specification

import grails.testing.web.interceptor.InterceptorUnitTest

import pingcrm.interceptor.SharedDataInterceptor

class SharedDataInterceptorSpec extends Specification implements InterceptorUnitTest<SharedDataInterceptor> {

    void 'shared data interceptor matches all requests'() {

        given: 'a request that should match the interceptor'
            withRequest(controller: 'anyController')

        expect: 'the interceptor to match'
            interceptor.doesMatch()
    }
}
