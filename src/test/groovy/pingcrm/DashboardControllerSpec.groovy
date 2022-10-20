package pingcrm

import pingcrm.controller.DashboardController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class DashboardControllerSpec extends Specification implements ControllerUnitTest<DashboardController> {

    void 'it is possible to reach the dashboard page'() {

        when:
        controller.index()

        then:
        status == 200
    }
}