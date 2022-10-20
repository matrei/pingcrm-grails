package pingcrm.pages


import geb.Page
import pingcrm.modules.PaginationModule
import pingcrm.modules.TableModule

class OrganizationListPage extends Page {

    static url = '/organizations'
    static at = { waitFor { 'Organizations' == js.exec('return document.querySelector("h1").textContent') } }

    static content = {
        table { $('table.w-full.whitespace-nowrap').module(TableModule) }
        pagination { $('div', class: 'mt-6').last().module(PaginationModule) }
        flashError(required: false) { $('div.bg-red-400').text() }
        flashSuccess(required: false) { $('div.bg-green-500').text() }
    }
}
