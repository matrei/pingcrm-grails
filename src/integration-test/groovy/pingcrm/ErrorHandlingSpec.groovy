package pingcrm

import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import pingcrm.pages.DashboardPage
import pingcrm.pages.Error403Page
import pingcrm.pages.Error404Page
import pingcrm.pages.Error500Page
import pingcrm.pages.LoginPage
import spock.lang.Unroll

@Integration
class ErrorHandlingSpec extends GebSpec {

    @Unroll
    void 'it renders the #status error page'(page) {

        given: 'a logged in demo user'
        def loginPage = to LoginPage
        loginPage.loginDemoUser()
        at DashboardPage

        when: 'going to a page that throws an exception'
        go page.url

        then: 'the user is redirected to the 500 page'
        at page

        where:
        status | page
        500    | Error500Page
        404    | Error404Page
        403    | Error403Page
    }
}
