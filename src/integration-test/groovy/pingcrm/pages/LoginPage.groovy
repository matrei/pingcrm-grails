package pingcrm.pages

import geb.Page
import geb.module.Checkbox
import geb.module.EmailInput
import geb.module.PasswordInput
import geb.module.TextInput

class LoginPage extends Page {

    static url = '/login'
    static at = { waitFor { 'Welcome Back!' == js.exec('return document.querySelector("h1").textContent') } }

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
