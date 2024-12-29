package pingcrm

import grails.plugin.geb.ContainerGebSpec
import grails.testing.mixin.integration.Integration
import pingcrm.pages.*
import spock.lang.Unroll

@Integration
class ErrorHandlingSpec extends ContainerGebSpec {

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
