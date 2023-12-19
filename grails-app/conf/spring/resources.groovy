import grails.plugin.inertia.ssr.ServerSideRenderer
import pingcrm.AppUserDetailsService
import pingcrm.SessionTracker
import pingcrm.SquigglyObjectMapperFactory
import pingcrm.UserPasswordEncoderListener
import pingcrm.controller.MaxFileUploadSizeResolver
import pingcrm.controller.ResourceCachingConfig
import pingcrm.image.ScalrImageProcessor
import pingcrm.security.web.authentication.rememberme.SameSiteTokenBasedRememberMeServices
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

beans = {

    userPasswordEncoderListener(UserPasswordEncoderListener)
    userDetailsService(AppUserDetailsService)
    sessionTracker(SessionTracker)
    multipartResolver(MaxFileUploadSizeResolver)
    imageProcessor(ScalrImageProcessor)

    /*
        This is needed because we don't want Spring Security to treat our Inertia requests as Ajax calls.
        Client side Inertia will expect regular responses.
    */
    def securityConfig = SpringSecurityUtils.securityConfig
    authenticationSuccessHandler(SavedRequestAwareAuthenticationSuccessHandler) {
        requestCache = ref('requestCache')
        redirectStrategy = ref('redirectStrategy')
        defaultTargetUrl = securityConfig.successHandler.defaultTargetUrl
        alwaysUseDefaultTargetUrl = securityConfig.successHandler.alwaysUseDefault
        targetUrlParameter = securityConfig.successHandler.targetUrlParameter
        useReferer = securityConfig.successHandler.useReferer
    }

    /*
        The spring security rememberMeServices is overridden to be able to set the same-site attribute on the remember-me cookie.
        This was not possible with a simple config setting at the time of writing this application.
    */
    rememberMeServices(SameSiteTokenBasedRememberMeServices, securityConfig.rememberMe.key, ref('userDetailsService')) {
        cookieName = securityConfig.rememberMe.cookieName
        sameSite = securityConfig.rememberMe.sameSite
    }

    /*
        This is a Jackson ObjectMapper that in cooperation with Squiggly (https://github.com/NicklasWallgren/squiggly)
        will help us select which properties to send to the client side.
    */
    publicDataMapper(SquigglyObjectMapperFactory)

    serverSideRenderer(ServerSideRenderer)

    resourceCachingConfig(ResourceCachingConfig)
}
