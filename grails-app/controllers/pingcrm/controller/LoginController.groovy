package pingcrm.controller

import groovy.transform.CompileStatic
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.session.SessionAuthenticationException

@CompileStatic
class LoginController extends grails.plugin.springsecurity.LoginController {

    def index() { renderInertia'Auth/Login' }

    def authAjax() { redirect controller: 'login' }

    def authfail() {

        def errors = [:]
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]

        if(exception) {
            switch(exception) {
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
}