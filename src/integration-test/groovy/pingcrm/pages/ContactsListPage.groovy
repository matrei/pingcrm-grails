package pingcrm.pages

import pingcrm.modules.PaginationModule
import pingcrm.modules.TableModule

class ContactsListPage extends BasePage {

    static heading = 'Contacts'
    static url = '/contacts'

    static content = {
        table { $('table.w-full.whitespace-nowrap').module(TableModule) }
        pagination { $('div', class: 'mt-6').last().module(PaginationModule) }
    }
}
