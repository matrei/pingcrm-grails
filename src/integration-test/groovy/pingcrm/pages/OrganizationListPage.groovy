package pingcrm.pages

import pingcrm.modules.PaginationModule
import pingcrm.modules.TableModule

class OrganizationListPage extends BasePage {

    static heading = 'Organizations'
    static url = '/organizations'

    static content = {
        table { $('table.w-full.whitespace-nowrap').module(TableModule) }
        pagination { $('div', class: 'mt-6').last().module(PaginationModule) }
    }
}
