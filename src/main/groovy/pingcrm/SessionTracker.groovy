package pingcrm

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import org.springframework.security.core.context.SecurityContext

import javax.servlet.http.HttpSession
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY

/**
 * Keeps track of http sessions and provides a way to invalidate all sessions for a particular user.
 * This is used to log out users from the system if they are deleted.
 */
@CompileStatic
class SessionTracker implements HttpSessionListener {

    private static final ConcurrentMap<Serializable, HttpSession> sessions = new ConcurrentHashMap<>()

    void sessionCreated(HttpSessionEvent sessionEvent) {
        sessions.put(sessionEvent.session.id, sessionEvent.session)
    }

    void sessionDestroyed(HttpSessionEvent sessionEvent) {
        sessions.remove(sessionEvent.session.id)
    }

    static void invalidateSessionsForUserId(Serializable id) {
        sessions.each {sessionId, session ->
            def sc = session.getAttribute(SPRING_SECURITY_CONTEXT_KEY) as SecurityContext
            def user = sc?.authentication?.principal as GrailsUser
            if(!user || user.id == id) {
                session.invalidate()
            }
        }
    }
}
