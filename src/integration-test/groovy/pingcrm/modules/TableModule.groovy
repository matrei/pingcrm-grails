package pingcrm.modules

import geb.Module

class TableModule extends Module {

    static content = {
        rows {
            $('tr', 'class': 'hover:bg-slate-100')
        }

        rowCount {
            rows.size()
        }
    }
}
