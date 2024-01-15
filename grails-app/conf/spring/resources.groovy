import pingcrm.AppUserDetailsService
import pingcrm.SessionTracker
import pingcrm.UserPasswordEncoderListener
import pingcrm.controller.MaxFileUploadSizeResolver
import pingcrm.controller.ResourceCachingConfig
import pingcrm.security.web.authentication.rememberme.SameSiteTokenBasedRememberMeServices
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

beans = {

    userPasswordEncoderListener(UserPasswordEncoderListener)
    userDetailsService(AppUserDetailsService)
    sessionTracker(SessionTracker)
    multipartResolver(MaxFileUploadSizeResolver)

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
}
