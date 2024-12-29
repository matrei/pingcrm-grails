package pingcrm

import grails.plugin.geb.ContainerGebSpec
import grails.testing.mixin.integration.Integration
import pingcrm.pages.*
import spock.lang.Unroll

@Integration
class MainMenuSpec extends ContainerGebSpec {

    @Unroll
    void 'being on a page highlights the corresponding menu item'(testPage) {

        given: 'a logged in user'
        def loginPage = to LoginPage
        loginPage.loginDemoUser()
        at DashboardPage

        when: 'going to a page'
        def page = to testPage

        then: 'the corresponding menu item is highlighted'
        waitFor { page.menuItemIsSelected() }

        where: 'the pages are'
        testPage << [DashboardPage, OrganizationListPage, ContactsListPage, ReportsPage, UserListPage, AboutPage]
    }
}
