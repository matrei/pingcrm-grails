package pingcrm


import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import pingcrm.pages.DashboardPage
import pingcrm.pages.LoginPage
import pingcrm.pages.UserCreatePage
import pingcrm.pages.UserEditPage
import pingcrm.pages.UserListPage

@Integration
class LoginSpec extends GebSpec {

    void 'accessing the application without logging in is not allowed'() {

        given: 'the user is not logged in'
        go '/logoff'

        when: 'trying to access the dashboard page without logging in'
        via securedPage

        then: 'the user is redirected to the login page'
        at LoginPage

        where:
        securedPage << [
                DashboardPage,
                UserCreatePage,
                UserEditPage,
                UserListPage
        ]
    }

    void 'the demo user can log in'() {

        when: 'the demo user tries to log in'
        def loginPage = to LoginPage
        loginPage.loginDemoUser()

        then: 'the demo demo user is logged in'
        at DashboardPage
    }

    void 'users can log in'() {

        given: 'a logged in demo user'
        def loginPage = to LoginPage
        loginPage.loginDemoUser()
        at DashboardPage

        and: 'some user data'
        def firstname = 'Luke'
        def lastname = 'Skywalker'
        def email = 'luke@usetheforce.com'
        def password = 'secret'

        when: 'creating a new user'
        def userCreatePage = to UserCreatePage
        userCreatePage.createUser firstname, lastname, email, password
        at UserListPage

        and: 'logging off'
        go '/logoff'
        at LoginPage

        and: 'trying to login with the credentials of the new user'
        loginPage = at LoginPage
        loginPage.login email, password

        then: 'the user is logged in'
        def dashboardPage = to DashboardPage
        dashboardPage.navbarName == "$firstname $lastname"
    }

    void 'trying to login with bad credentials fails'() {

        when: 'bad credentials are used at login'
        def loginPage = to LoginPage
        loginPage.login 'abc@123.se', 'abc123'

        then: 'the user is still at the login page'
        at loginPage

        and: 'an error message is shown'
        waitFor { loginPage.formError }
    }
}
