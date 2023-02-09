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
        String cookieValue = encodeCookie(tokens);

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
        logger.debug("Cancelling cookie");

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
        request.contextPath ?: '/'
    }

    @Override
    void setCookieDomain(String cookieDomain) {
        super.setCookieDomain cookieDomain
        this.cookieDomain = cookieDomain
    }

    @Override
    void setUseSecureCookie(boolean useSecureCookie) {
        super.setUseSecureCookie useSecureCookie
        this.useSecureCookie = useSecureCookie
    }

    void setSameSite(String sameSite) {
        this.sameSite = sameSite
    }

    String getSameSite() {
        this.sameSite
    }
}
