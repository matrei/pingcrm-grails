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
package pingcrm.security

import groovy.transform.CompileStatic

import jakarta.servlet.DispatcherType

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher

import grails.compiler.beans.GrailsBeans

import pingcrm.AppUserDetailsService
import pingcrm.SessionTracker
import pingcrm.UserPasswordEncoderListener
import pingcrm.security.web.authentication.rememberme.SameSiteTokenBasedRememberMeServices

/**
 * The Spring Security configuration of the application.
 *
 * @author Mattias Reichel
 * @since 5.0.0
 */
@GrailsBeans
@CompileStatic
@Configuration(proxyBeanMethods = false)
class SecurityConfig {

    static final String LOGIN_URL = '/login'

    static beans = {

        field('rememberMeKey', String).value('pingcrm.security.rememberMe.key')
        field('rememberMeCookieName', String).value('pingcrm.security.rememberMe.cookieName', 'remember-me')
        field('rememberMeSameSite', String).value('pingcrm.security.rememberMe.sameSite', 'Lax')

        // Spring Security's default delegating encoder (currently BCrypt),
        // which stores the algorithm with the hash, e.g. {bcrypt}$2a$10$...
        bean('passwordEncoder', PasswordEncoder) {
            PasswordEncoderFactories.createDelegatingPasswordEncoder()
        }

        // Encodes the password of a user before it is persisted
        bean(UserPasswordEncoderListener)

        // Keeps track of sessions so all sessions of a deleted user can be invalidated
        bean(SessionTracker)

        // Sets the same-site attribute on the remember-me cookie
        bean('rememberMeServices', RememberMeServices) { AppUserDetailsService appUserDetailsService ->
            new SameSiteTokenBasedRememberMeServices(rememberMeKey, appUserDetailsService).tap {
                cookieName = rememberMeCookieName
                sameSite = rememberMeSameSite
            }
        }

        // Saves all requests (also Inertia requests, which look like XMLHttpRequests)
        // so the user can be redirected to the requested page after logging in
        bean('requestCache', RequestCache) {
            new HttpSessionRequestCache(matchingRequestParameterName: null)
        }

        bean('securityFilterChain', SecurityFilterChain) { HttpSecurity http, AppUserDetailsService appUserDetailsService, RememberMeServices rememberMeServices, RequestCache requestCache ->
            http
                .userDetailsService(appUserDetailsService)
                .authorizeHttpRequests {
                    it
                        // error pages and the forward to the access denied page
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        // Add '/h2-console/**' to enable access to the h2 database console
                        // (also look at spring.h2.console.enabled in application.yml)
                        .requestMatchers(LOGIN_URL, '/login/**', '/static/**', '/favicon.ico', '/error').permitAll()
                        .requestMatchers(
                                '/', '/about', '/reports', '/img/**', '/test-500-error',
                                '/contacts', '/contacts/**',
                                '/organizations', '/organizations/**',
                                '/users', '/users/**'
                        ).authenticated()
                        // Everything else is forbidden (or redirects to the login page if not logged in)
                        .anyRequest().denyAll()
                }
                .formLogin {
                    it
                        .loginPage(LOGIN_URL)
                        .loginProcessingUrl('/login/authenticate')
                        .failureUrl('/login/authfail?login_error=1')
                }
                .rememberMe {
                    it
                        .rememberMeServices(rememberMeServices)
                        .key(rememberMeKey)
                }
                .logout {
                    it
                        // Log off with any HTTP method (the UI uses DELETE)
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher('/logoff'))
                        .logoutSuccessUrl('/')
                }
                .exceptionHandling {
                    it
                        // Redirect to the login page for all requests,
                        // also Inertia requests (which look like XMLHttpRequests)
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(LOGIN_URL))
                        .accessDeniedPage('/login/denied')
                }
                .requestCache {
                    it.requestCache(requestCache)
                }
                // CSRF protection is handled by the grails-csrf plugin
                .csrf {
                    it.disable()
                }
                .build()
        }
    }
}
