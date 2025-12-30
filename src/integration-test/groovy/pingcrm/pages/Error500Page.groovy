package pingcrm.pages

import geb.Page

class Error500Page extends Page {

    static heading = '500: Server Error'
    static url = '/test-500-error'

    static at = {
        heading == js.exec(
                'return document.querySelector("h1").textContent'
        )
    }

}
