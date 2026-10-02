/*
 * Copyright 2026-present original authors
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
package pingcrm.security

import groovy.transform.CompileStatic

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import io.github.matrei.grailsplugin.csrf.CsrfFailureHandler
import io.github.matrei.grailsplugin.csrf.CsrfFailureReason

/**
 * Tells the user that the page has expired when a request fails CSRF protection
 * because the session has no token, typically because the session has expired.
 * Other failures are rejected with {@code 403 Forbidden}.
 *
 * @author Mattias Reichel
 * @since 5.0.0
 */
@CompileStatic
class PageExpiredCsrfFailureHandler implements CsrfFailureHandler {

    static final String PAGE_EXPIRED_URI = '/page-expired'

    @Override
    void handle(HttpServletRequest request, HttpServletResponse response, CsrfFailureReason reason) {
        if (reason == CsrfFailureReason.MISSING_STORED_TOKEN) {
            request.getRequestDispatcher(PAGE_EXPIRED_URI).forward(request, response)
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, reason.message)
        }
    }
}
