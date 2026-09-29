import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

import grails.plugin.springsecurity.SpringSecurityUtils

import pingcrm.AppUserDetailsService
import pingcrm.SessionTracker
import pingcrm.UserPasswordEncoderListener
import pingcrm.controller.ResourceCachingConfig
import pingcrm.i18n.SupportedLocaleResolver
import pingcrm.security.web.authentication.rememberme.SameSiteTokenBasedRememberMeServices

beans = {

    userPasswordEncoderListener(UserPasswordEncoderListener)
    userDetailsService(AppUserDetailsService)
    sessionTracker(SessionTracker)

    /*
        This is needed because we don't want Spring Security to treat our Inertia requests as Ajax calls.
        Client side Inertia will expect regular responses.
    */
    def securityConfig = SpringSecurityUtils.securityConfig.flatten()
    authenticationSuccessHandler(SavedRequestAwareAuthenticationSuccessHandler) {
        requestCache = ref('requestCache')
        redirectStrategy = ref('redirectStrategy')
        defaultTargetUrl = securityConfig.get('successHandler.defaultTargetUrl')
        alwaysUseDefaultTargetUrl = securityConfig.get('successHandler.alwaysUseDefault')
        targetUrlParameter = securityConfig.get('successHandler.targetUrlParameter')
        useReferer = securityConfig.get('successHandler.useReferer')
    }

    /*
        The spring security rememberMeServices is overridden to be able to set the same-site attribute on the remember-me cookie.
        This was not possible with a simple config setting at the time of writing this application.
    */
    rememberMeServices(SameSiteTokenBasedRememberMeServices, securityConfig.get('rememberMe.key'), ref('userDetailsService')) {
        cookieName = securityConfig.get('rememberMe.cookieName')
        sameSite = securityConfig.get('rememberMe.sameSite')
    }

    resourceCachingConfig(ResourceCachingConfig)

    /*
        Resolve the locale from the Accept-Language header (or ?lang= for the session),
        but only to the locales the UI is translated into.
    */
    def uiLocales = application.config.getProperty('pingcrm.i18n.locales', List, ['en']).collect {
        Locale.forLanguageTag(it as String)
    }
    localeResolver(SupportedLocaleResolver, uiLocales, uiLocales.first())
}
