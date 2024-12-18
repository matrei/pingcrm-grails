import grails.plugin.inertia.Inertia

import javax.servlet.http.HttpServletRequest

grails {
    plugin {
        springsecurity {
            ajaxHeader = '' // Disable default header based ajax matching
            ajaxCheckClosure = { HttpServletRequest request ->
                /** Enable inertia aware ajax:
                 * Inertia requests _LOOK_ like ajax, but are meant to be treated like standard navigation */
                if ('XMLHttpRequest' == request.getHeader('X-Requested-With')) {
                    return !Inertia.isInertiaRequest
                }
                return false
            }
        }
    }
}