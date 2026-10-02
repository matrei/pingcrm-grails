/*
* Copyright 2023-present original authors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package pingcrm.controller

import groovy.transform.CompileStatic

import org.springframework.web.servlet.ModelAndView

import grails.plugin.inertia.Inertia
import grails.util.Environment

import org.grails.web.util.GrailsApplicationAttributes

import pingcrm.i18n.LocalizedMessages

/**
 * A controller for handling errors.
 * @author Mattias Reichel
 * @since 3.0.0
 */
@CompileStatic
class ErrorHandlingController implements LocalizedMessages {

    def forbidden() {
        render('forbidden')
    }

    def notFound() {
        respondWith(404)
    }

    def internalServerError() {
        respondWith(500)
    }

    /**
     * Forwarded to by {@link pingcrm.security.PageExpiredCsrfFailureHandler}.
     */
    def pageExpired() {
        flash.error = localizedMessage('flash.csrf.expired')
        // Redirect with 303 See Other, so the browser follows the redirect with a GET request
        var url = request.getHeader('referer') ?: '/login'
        response.status = 303
        response.setHeader('Location', url)
        request.setAttribute(GrailsApplicationAttributes.REDIRECT_ISSUED, url)
    }

    def serviceUnavailable() {
        render('serviceUnavailable')
    }

    private ModelAndView respondWith(int status, String description = null) {

        /*
         * If we're in development mode, we let Grails handle the error pages.
         * This will pop up in a modal window, which is useful for debugging.
         */
        if (development) {
            Inertia.cancel()
            return new ModelAndView(viewName: status == 404 ? '/notFound': '/error')
        }

        /*
         * If we're in production mode, we render the error page using Inertia.
         * This will display the error page as a normal page, which is more user-friendly.
         */
        var desc = description ?: request.getAttribute('jakarta.servlet.error.message')
        renderInertia('Error/Index', [status: status, description: desc])
    }

    private static boolean isDevelopment() {
        Environment.DEVELOPMENT == Environment.current
    }
}
