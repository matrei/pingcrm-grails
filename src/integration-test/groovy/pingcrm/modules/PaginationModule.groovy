package pingcrm.modules

import geb.Module

class PaginationModule extends Module {

    static content = {
        steps { $('div', class: 'mr-1') }
    }
}
