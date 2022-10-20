package pingcrm.pages

import geb.Page

class DashboardPage extends Page {

    static url = '/'
    static heading = 'Dashboard'
    static at = { waitFor { heading == js.exec('return document.querySelector("h1").textContent') } }

    static content = {
        navbarName { $('button[type=button].mt-1', 0).text() }
    }

}
