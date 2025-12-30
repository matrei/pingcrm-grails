package pingcrm

import spock.lang.Unroll

import grails.plugin.geb.ContainerGebSpec
import grails.testing.mixin.integration.Integration

import pingcrm.pages.AboutPage
import pingcrm.pages.BasePage
import pingcrm.pages.ContactsListPage
import pingcrm.pages.DashboardPage
import pingcrm.pages.LoginPage
import pingcrm.pages.OrganizationListPage
import pingcrm.pages.ReportsPage
import pingcrm.pages.UserListPage

@Integration
class MainMenuSpec extends ContainerGebSpec {

    @Unroll
    void 'being on a page highlights the corresponding menu item'(Class<BasePage> testPageClass) {

        given: 'a logged in user'
        def loginPage = to(LoginPage)
        loginPage.loginDemoUser()
        at(DashboardPage)

        when: 'going to a page'
        def page = to(testPageClass)

        then: 'the corresponding menu item is highlighted'
        waitFor { page.menuItemIsSelected }

        where: 'the pages are'
        testPageClass << [DashboardPage, OrganizationListPage, ContactsListPage, ReportsPage, UserListPage, AboutPage]
    }
}
