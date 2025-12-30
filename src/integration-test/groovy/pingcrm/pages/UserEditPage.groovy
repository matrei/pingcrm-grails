package pingcrm.pages

import geb.Page
import geb.module.FileInput

class UserEditPage extends Page {

    Long userId
    boolean testServerMaxUploadSizeValidation = false

    private static final Map<String, String> selectors = [
            'avatarImage': 'img.w-8.h-8.rounded-full',
            'deleteBtn': 'button.text-red-600',
            'flashError': 'div.bg-red-400',
            'flashSuccess': 'div.bg-green-500'

    ]

    UserEditPage(Long userId) {
        this.userId = userId
    }

    static url = '/users'

    String convertToPath(Object[] args) {
        "/$userId/edit${testServerMaxUploadSizeValidation ? '?testServerMaxUploadSizeValidation' : ''}"
    }

    static btnText = 'Update User'

    static at = {
        btnText == js.exec(
                'return document.querySelector("[type=submit]").textContent'
        )
        submitBtn.displayed // Without this I have gotten StaleElementReferenceException
    }

    static content = {
        submitBtn {
            $(type: 'submit')
        }

        deleteBtn {
            $(selectors.deleteBtn)
        }

        flashError(required: false) {
            waitFor {
                $(selectors.flashError).displayed
            }
            $(selectors.flashError)
                    .text()
        }

        flashSuccess(required: false) {
            waitFor {
                $(selectors.flashSuccess).displayed
            }
            $(selectors.flashSuccess)
                    .text()
        }

        photoField {
            $('input', type: 'file')
                    .module(FileInput)
        }

        photoFieldBorder {
            photoField.parent()
        }

        avatarImage(required: false) {
            waitFor {
                $(selectors.avatarImage)
                        ?.firstElement()
                        ?.displayed
            }
            $(selectors.avatarImage)
                    .firstElement()
        }

        avatarImageSrc(required: false) {
            waitFor {
                $(selectors.avatarImage)
                        ?.firstElement()
                        ?.displayed
            }
            $(selectors.avatarImage)
                    .firstElement()
                    .getDomProperty('src')
        }

        avatarImageFilename(required: false) {
            def src = avatarImageSrc
            src.substring(
                    src.lastIndexOf('/') + 1,
                    src.indexOf('?')
            )
        }
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    void save() {
        submitBtn.click()
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    void delete() {
        deleteBtn.click()
    }
}