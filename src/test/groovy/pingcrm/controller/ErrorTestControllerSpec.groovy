package pingcrm.controller

import spock.lang.Specification

import grails.testing.web.controllers.ControllerUnitTest

class ErrorTestControllerSpec extends Specification implements ControllerUnitTest<ErrorTestController> {

    void 'controller action generates an error'() {

        when: 'calling the action that should generate an error'
            controller.generateInternalServerError()

        then: 'the error is handled by the error page'
            response.status == 500
    }
}
