package pingcrm.interceptor

import spock.lang.Specification

import grails.testing.web.interceptor.InterceptorUnitTest

class EditPreventionInterceptorSpec extends Specification implements InterceptorUnitTest<EditPreventionInterceptor> {

    void 'it matches the correct requests'() {

        given: 'A request matches the interceptor'
            withRequest(controller: controller, action: action)

        expect: 'The interceptor does match'
            matches == interceptor.doesMatch()

        where:
            controller     | action       | matches
            'users'        | 'updateUser' | true
            'users'        | 'delete'     | true
            'users'        | 'edit'       | false
            'contacts'     | 'delete'     | false
            'asdf'         | 'delete'     | false
            'asdf'         | 'asdf'       | false
    }
}
