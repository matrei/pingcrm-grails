package pingcrm.controller

import grails.plugin.inertia.Inertia
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Environment
import groovy.transform.CompileStatic
import org.springframework.web.servlet.ModelAndView

@CompileStatic
@Secured('permitAll')
class ErrorHandlingController {

    def forbidden() {
        render 'forbidden'
    }

    def notFound() {
        respondWith 404
    }

    def internalServerError() {
        respondWith 500
    }

    def serviceUnavailable() {
        render 'serviceUnavailable'
    }

    private ModelAndView respondWith(int status, String description = null) {

        /*
         * If we're in development mode, we let Grails handle the error pages.
         * This will pop up in a modal window, which is useful for debugging.
         */
        if(development) {
            Inertia.cancel()
            return new ModelAndView(viewName: status == 404 ? '/notFound': '/error')
        }

        /*
         * If we're in production mode, we render the error page using Inertia.
         * This will display the error page as a normal page, which is more user-friendly.
         */
        def desc = description ?: request.getAttribute('javax.servlet.error.message')
        renderInertia 'Error/Index', [status: status, description: desc]
    }

    private static boolean isDevelopment() {
        Environment.DEVELOPMENT == Environment.current
    }
}
