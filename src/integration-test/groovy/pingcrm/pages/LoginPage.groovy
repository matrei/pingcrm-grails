package pingcrm.pages

import geb.Page
import geb.url.UrlFragment
import geb.module.Checkbox
import geb.module.EmailInput
import geb.module.PasswordInput

class LoginPage extends Page implements LocalizedPage {

    static heading = [en: 'Welcome Back!', sv: 'Välkommen tillbaka!']
    static url = '/login'

    static at = {
        delegate.expectedHeading == js.exec(
                'return document.querySelector("h1").textContent'
        )
        emailInput.displayed // Without this I have gotten StaleElementReferenceException
    }


    static content = {
        emailInput {
            $(type: 'email')
                    .module(EmailInput)
        }

        passwordInput {
            $(type: 'password')
                    .module(PasswordInput)
        }

        rememberMeInput {
            $(type: 'checkbox')
                    .module(Checkbox)
        }

        formError {
            js.exec(
                    'return document.querySelector("div.form-error")'
            )
        }

        loginBtn {
            $(type: 'submit')
                    .firstElement()
        }
    }

    void login(String email, String password, boolean rememberMe = false) {
        emailInput.text = email
        passwordInput.text = password
        rememberMeInput.value(rememberMe)
        loginBtn.click()
        // Wait for the login request to finish (the browser leaves the login page or shows an error).
        // Navigating away before that aborts the request, and the new session cookie from logging in is lost.
        waitFor {
            js.exec('return document.querySelector("h1")?.textContent') != expectedHeading ||
                    js.exec('return document.querySelector("div.form-error") !== null')
        }
    }

    void loginDemoUser() {
        login('johndoe@example.com', 'secret')
    }

    @Override
    void to(Map params, UrlFragment fragment, Object[] args) {
        super.to(withLangParam(params), fragment, args)
    }
}
