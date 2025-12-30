package pingcrm.pages

import geb.Page

class Error403Page extends Page {

    static heading = '403: Forbidden'
    static url = '/hello'

    static at = {
        heading == js.exec(
                'return document.querySelector("h1").textContent'
        )
    }

}
