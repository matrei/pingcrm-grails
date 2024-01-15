/*
* Copyright 2023 original authors
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

import grails.plugin.inertia.Inertia
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Environment
import groovy.transform.CompileStatic
import org.springframework.web.servlet.ModelAndView

/**
 * A controller for handling errors.
 * @author Mattias Reichel
 * @since 3.0.0
 */
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
