package pingcrm.interceptor

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

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
