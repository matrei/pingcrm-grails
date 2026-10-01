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
package pingcrm

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import groovy.transform.CompileStatic

import jakarta.servlet.http.HttpSession
import jakarta.servlet.http.HttpSessionEvent
import jakarta.servlet.http.HttpSessionIdListener
import jakarta.servlet.http.HttpSessionListener

import org.springframework.security.core.context.SecurityContext

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY

/**
 * Keeps track of http sessions and provides a way to invalidate all sessions for a particular user.
 * This is used to log out users from the system if they are deleted.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class SessionTracker implements HttpSessionListener, HttpSessionIdListener {

    private static final ConcurrentMap<Serializable, HttpSession> sessions = new ConcurrentHashMap<>()

    @Override
    void sessionCreated(HttpSessionEvent sessionEvent) {
        sessions.put(sessionEvent.session.id, sessionEvent.session)
    }

    @Override
    void sessionDestroyed(HttpSessionEvent sessionEvent) {
        sessions.remove(sessionEvent.session.id)
    }

    /**
     * Keeps track of sessions that get a new id, e.g. when logging in
     * (Spring Security's default session fixation protection changes the session id).
     */
    @Override
    void sessionIdChanged(HttpSessionEvent sessionEvent, String oldSessionId) {
        sessions.remove(oldSessionId)
        sessions.put(sessionEvent.session.id, sessionEvent.session)
    }

    static void invalidateSessionsForUserId(Serializable id) {
        sessions.each { Serializable sessionId, HttpSession session ->
            var sc = session.getAttribute(SPRING_SECURITY_CONTEXT_KEY) as SecurityContext
            var user = sc?.authentication?.principal as AppUserDetails
            if (!user || user.id == id) {
                session.invalidate()
            }
        }
    }
}
