package pingcrm.pages

import geb.Page
import geb.module.Checkbox
import geb.module.EmailInput
import geb.module.PasswordInput

class LoginPage extends Page {

    static heading = 'Welcome Back!'
    static url = '/login'
    static at = { waitFor {
        heading == js.exec('return document.querySelector("h1").textContent')
        emailInput.displayed  // Without this I have gotten StaleElementReferenceException
    } }


    static content = {
        emailInput { $(type: 'email').module(EmailInput) }
        passwordInput { $(type: 'password').module(PasswordInput) }
        rememberMeInput { $(type: 'checkbox').module(Checkbox) }
        formError { js.exec('return document.querySelector("div.form-error")') }
        loginBtn { $(type: 'submit').firstElement() }
    }

    void login(String email, String password, boolean rememberMe = false) {
        emailInput.text = email
        passwordInput.text = password
        rememberMeInput.value(rememberMe)
        loginBtn.click()
    }

    void loginDemoUser() {
        login('johndoe@example.com', 'secret')
    }
}
