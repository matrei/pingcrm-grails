/*
 * Copyright 2022-present original authors
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

import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.session.SessionAuthenticationException

import pingcrm.i18n.LocalizedMessages

/**
 * A controller for the login page and for showing login failures
 * and access denied errors (the login itself is handled by Spring Security).
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class LoginController implements LocalizedMessages {

    def index() {
        renderInertia('Auth/Login')
    }

    def authfail() {

        def errors = [:]
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]

        if (exception) {
            switch (exception) {
                case AccountExpiredException:        errors.username = localizedMessage('login.error.expired'); break
                case CredentialsExpiredException:    errors.password = localizedMessage('login.error.passwordExpired'); break
                case DisabledException:              errors.username = localizedMessage('login.error.disabled'); break
                case LockedException:                errors.username = localizedMessage('login.error.locked'); break
                case SessionAuthenticationException: errors.username = localizedMessage('login.error.maxSessionsExceeded'); break
                default:                             errors.username = localizedMessage('login.error.fail'); break
            }
        }

        chain(controller: 'login', model: [errors: errors])
    }

    def denied() {
        renderInertia('Error/Index', [status: 403])
    }
}
