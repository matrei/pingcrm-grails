/*
 * Copyright 2022-2024 original authors
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
package pingcrm.security.web.authentication.rememberme

import groovy.transform.CompileStatic
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This class provides the ability to set the same-site attribute on the remember-me cookie.
 * This was not possible with spring security at the time of writing this application.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class SameSiteTokenBasedRememberMeServices extends TokenBasedRememberMeServices {

    private String cookieDomain
    private boolean useSecureCookie
    private String sameSite = 'Lax'

    SameSiteTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService)
    }

    @Override
    protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
        String cookieValue = encodeCookie(tokens)
        ResponseCookie rememberMeCookie = ResponseCookie
            .from(cookieName, cookieValue)
            .maxAge(maxAge)
            .path(getCookiePath(request))
            .domain(cookieDomain)
            .secure(useSecureCookie ?: request.isSecure())
            .httpOnly(true)
            .sameSite(sameSite)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, rememberMeCookie.toString())
    }

    @Override
    protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
        logger.debug('Cancelling cookie')
        ResponseCookie rememberMeCookie = ResponseCookie
            .from(cookieName, null)
            .maxAge(0)
            .path(getCookiePath(request))
            .domain(cookieDomain)
            .secure(useSecureCookie ?: request.isSecure())
            .httpOnly(true)
            .sameSite(sameSite)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, rememberMeCookie.toString())
    }

    private static String getCookiePath(HttpServletRequest request) {
        return request.contextPath ?: '/'
    }

    @Override
    void setCookieDomain(String cookieDomain) {
        super.setCookieDomain cookieDomain
        this.cookieDomain = cookieDomain
    }

    @Override
    void setUseSecureCookie(boolean useSecureCookie) {
        super.setUseSecureCookie(useSecureCookie)
        this.useSecureCookie = useSecureCookie
    }

    void setSameSite(String sameSite) {
        this.sameSite = sameSite
    }

    String getSameSite() {
        this.sameSite
    }
}
