package pingcrm

import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import pingcrm.pages.*
import spock.lang.Unroll

@Integration
class MainMenuSpec extends GebSpec {

    @Unroll
    void 'being on a page highlights the corresponding menu item'(testPage) {

        given: 'a logged in user'
        def loginPage = to LoginPage
        loginPage.loginDemoUser()
        at DashboardPage

        when: 'going to a page'
        def page = to testPage

        then: 'the corresponding menu item is highlighted'
        page.menuItemIsSelected()

        where: 'the pages are'
        testPage << [DashboardPage, OrganizationListPage, ContactsListPage, ReportsPage, UserListPage, AboutPage]
    }
}
