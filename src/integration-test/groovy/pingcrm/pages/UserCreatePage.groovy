package pingcrm.pages

import geb.Page
import geb.module.EmailInput
import geb.module.FileInput
import geb.module.PasswordInput
import geb.module.Select
import geb.module.TextInput

class UserCreatePage extends Page {

    static url = '/users/create'
    static btnText = 'Create User'
    static at = { waitFor { btnText == js.exec('return document.querySelector("[type=submit]").textContent') } }

    static content = {
        firstNameField { $('form').find('input', 0).module(TextInput) }
        lastNameField { $('form').find('input', 1).module(TextInput) }
        emailField { $('form').find('input', 2).module(TextInput) }
        passwordField { $('form').find('input', 3).module(PasswordInput) }
        ownerSelect { $('form').find('select').module(Select) }
        photoField { $('form').find('input', type: 'file').module(FileInput) }
        photoFieldBorder { photoField.parent() }
        submitBtn { $(type: 'submit') }
        flashError(required: false) { $('div.bg-red-400').text() }
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    void createUser(String firstname, String lastname, String email, String password, File photo = null) {
        firstNameField.text = firstname
        lastNameField.text = lastname
        emailField.text = email
        passwordField.text = password
        if(photo) photoField.file = photo
        submitBtn.click()
    }
}