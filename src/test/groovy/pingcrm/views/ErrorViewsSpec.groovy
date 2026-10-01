package pingcrm.views

import spock.lang.Specification

import grails.testing.web.GrailsWebUnitTest
import org.grails.core.io.DefaultResourceLocator
import org.grails.web.errors.ErrorsViewStackTracePrinter

/**
 * The statically compiled error views (only rendered in development).
 */
class ErrorViewsSpec extends Specification implements GrailsWebUnitTest {

    def beans = {
        // Used by <g:renderException>, registered like the GSP plugin does in an application
        bean(ErrorsViewStackTracePrinter) {
            new ErrorsViewStackTracePrinter(new DefaultResourceLocator())
        }
    }

    void 'the error view renders the exception'() {

        given: 'the error attributes the servlet container sets for an error'
            request.setAttribute('jakarta.servlet.error.status_code', 500)
            request.setAttribute('jakarta.servlet.error.request_uri', '/contacts')
            request.setAttribute('jakarta.servlet.error.servlet_name', 'grailsDispatcherServlet')

        when: 'rendering the error view with an exception'
            var output = render(view: '/error', model: [exception: new IllegalStateException('Something broke')])

        then: 'the exception is rendered'
            output.contains('Something broke')
    }

    void 'the error view renders the error details when there is no exception'() {

        when: 'rendering the error view with error details'
            var output = render(view: '/error', model: [message: 'Bad request', path: '/contacts'])

        then: 'the details are rendered'
            output.contains('Message: Bad request')
            output.contains('Path: /contacts')
    }

    void 'the not found view renders the requested path'() {

        given: 'a request for a path'
            request.requestURI = '/does-not-exist'

        when: 'rendering the not found view'
            var output = render(view: '/notFound')

        then: 'the path is rendered'
            output.contains('Path: /does-not-exist')
    }
}
