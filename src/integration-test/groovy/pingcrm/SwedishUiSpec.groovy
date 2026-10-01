package pingcrm

import grails.plugin.geb.ContainerGebSpec
import grails.testing.mixin.integration.Integration

import pingcrm.pages.ContactsListPage
import pingcrm.pages.DashboardPage
import pingcrm.pages.LoginPage

@Integration
class SwedishUiSpec extends ContainerGebSpec {

    void cleanup() {
        // Do not leak the session language into other specs
        go('/logoff')
        browser.clearCookies()
    }

    void 'the UI can be used in Swedish'() {

        when: 'opening the login page in Swedish and logging in'
            var loginPage = to(new LoginPage(lang: 'sv'))
            loginPage.loginDemoUser()

        then: 'the dashboard is in Swedish'
            var dashboardPage = at(new DashboardPage(lang: 'sv'))

        when: 'navigating to the contacts page'
            dashboardPage.menuItem('/contacts').click()

        then: 'the contacts page is in Swedish'
            var contactsPage = at(new ContactsListPage(lang: 'sv'))
            contactsPage.table.columnHeaders == ['Namn', 'Organisation', 'Ort', 'Telefon']
    }
}
