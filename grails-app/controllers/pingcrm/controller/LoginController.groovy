/*
 * Copyright 2022-2023 original authors
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

/**
 * A controller for handling login/logout.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class LoginController extends grails.plugin.springsecurity.LoginController {

    @Override
    def index() { renderInertia 'Auth/Login' }

    @Override
    def authAjax() { redirect controller: 'login' }

    @Override
    def authfail() {

        def errors = [:]
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]

        if (exception) {
            switch (exception) {
                case AccountExpiredException:        errors.username = messageSource.getMessage 'springSecurity.errors.login.expired',               null, "Account Expired", request.locale; break
                case CredentialsExpiredException:    errors.password = messageSource.getMessage 'springSecurity.errors.login.passwordExpired',       null, "Password Expired", request.locale; break
                case DisabledException:              errors.username = messageSource.getMessage 'springSecurity.errors.login.disabled',              null, "Account Disabled", request.locale; break
                case LockedException:                errors.username = messageSource.getMessage 'springSecurity.errors.login.locked',                null, "Account Locked", request.locale; break
                case SessionAuthenticationException: errors.username = messageSource.getMessage 'springSecurity.errors.login.max.sessions.exceeded', null, "Sorry, you have exceeded your maximum number of open sessions.", request.locale; break
                default:                             errors.username = messageSource.getMessage 'springSecurity.errors.login.fail',                  null, "Authentication Failure", request.locale; break
            }
        }

        chain controller: 'login', model: [errors: errors]
    }

    @Override
    def denied() { renderInertia 'Error/Index', [status: 403] }

    @Override
    def ajaxDenied() { denied() }
}