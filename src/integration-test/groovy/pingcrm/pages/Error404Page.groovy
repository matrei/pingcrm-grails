package pingcrm.pages

import geb.Page

class Error404Page extends Page {

    static heading = '404: Page Not Found'
    static url = '/static/does-not-exist'

    static at = {
        heading == js.exec(
                'return document.querySelector("h1").textContent'
        )
    }

}
