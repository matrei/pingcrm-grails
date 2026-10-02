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

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Loads the CSRF token on every request, so that Spring Security sets the XSRF-TOKEN cookie,
 * which Inertia sends back in the X-XSRF-TOKEN header.
 * Spring Security otherwise only loads the token, and sets the cookie, when something uses it.
 *
 * @author Mattias Reichel
 * @since 5.0.0
 */
@CompileStatic
class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        var csrfToken = request.getAttribute(CsrfToken.name) as CsrfToken
        csrfToken?.token
        chain.doFilter(request, response)
    }
}
