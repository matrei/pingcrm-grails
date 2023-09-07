package pingcrm.controller

import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic

@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class ErrorTestController {

    def generateInternalServerError() {
        throw new RuntimeException('This error has been intentionally generated to show error handling.')
    }

    def handleRuntimeException(RuntimeException ex) {
        request.setAttribute 'javax.servlet.error.exception', ex
        response.sendError 500, ex.message
    }

}
