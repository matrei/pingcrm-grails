package pingcrm

import spock.lang.Unroll

import geb.Page

import grails.plugin.geb.ContainerGebSpec
import grails.testing.mixin.integration.Integration

import pingcrm.pages.DashboardPage
import pingcrm.pages.Error403Page
import pingcrm.pages.Error404Page
import pingcrm.pages.Error500Page
import pingcrm.pages.LoginPage

@Integration
class ErrorHandlingSpec extends ContainerGebSpec {

    @Unroll
    void 'it renders the #status error page'(int status, Class<? extends Page> errorPage) {

        given: 'a logged in demo user'
            to(LoginPage).loginDemoUser()
            at(DashboardPage)

        when: 'going to a page that responds with an error status'
            via(errorPage)

        then: 'the error page for that status is shown'
            at(errorPage)

        where:
            status | errorPage
            500    | Error500Page
            404    | Error404Page
            403    | Error403Page
    }
}
