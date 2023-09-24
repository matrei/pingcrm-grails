package pingcrm.pages

import geb.Page
import geb.module.FileInput

class UserEditPage extends Page {

    Long userId
    boolean testServerMaxUploadSizeValidation = false

    UserEditPage(Long userId) {
        this.userId = userId
    }

    static url = '/users'
    String convertToPath(Object[] args) { "/$userId/edit${testServerMaxUploadSizeValidation ? '?testServerMaxUploadSizeValidation' : ''}" }

    static btnText = 'Update User'
    static at = { waitFor {
        btnText == js.exec('return document.querySelector("[type=submit]").textContent')
        submitBtn.displayed // Without this I have gotten StaleElementReferenceException
    } }

    static content = {
        submitBtn(to: new UserEditPage(userId)) { $(type: 'submit') }
        deleteBtn { $('button.text-red-600') }
        flashError(required: false) { $('div.bg-red-400').text() }
        flashSuccess(required: false) { $('div.bg-green-500').text() }
        photoField { $('input', type: 'file').module(FileInput) }
        photoFieldBorder { photoField.parent() }
        avatarImage(required: false) { $('img.w-8.h-8.rounded-full').firstElement() }
        avatarImageSrc(required: false) { avatarImage.getAttribute('src') }
        avatarImageFilename(required: false) { avatarImageSrc.substring(avatarImageSrc.lastIndexOf('/')+1, avatarImageSrc.indexOf('?')) }
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    void save() { submitBtn.click() }

    @SuppressWarnings('GrMethodMayBeStatic')
    void delete() { deleteBtn.click() }
}