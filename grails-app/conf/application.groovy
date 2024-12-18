import grails.plugin.inertia.Inertia
grails {
    plugin {
       springsecurity {
            ajaxHeader = "" // Disable default header based ajax matching
            ajaxCheckClosure  = { request ->
                // Enable inertia aware ajax: Inertia requests _LOOK_ like ajax, but are meant to be treated like standard navigation
                if ("XMLHttpRequest" == request.getHeader("X-Requested-With")) {
                    return !(request.getHeader(Inertia.INERTIA_HEADER) as Boolean)
                }
                return false
            }
        }
    }
}