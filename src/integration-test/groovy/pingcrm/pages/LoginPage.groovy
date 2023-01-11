package pingcrm.pages

import geb.module.Checkbox
import geb.module.EmailInput
import geb.module.PasswordInput

class LoginPage extends BasePage {

    static heading = 'Welcome Back!'
    static url = '/login'

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
