package pingcrm.controller

import grails.plugin.springsecurity.annotation.Secured
import grails.util.Environment
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value

@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class AboutController {

    @Value('${info.app.name}') String name
    @Value('${info.app.version}') String version
    @Value('${info.app.grailsVersion}') String grailsVersion
    String environment

    AboutController() {
        this.environment = Environment.current.name
    }

    def index() {
        def appInfo = [
            name: name,
            version: version,
            grailsVersion: grailsVersion,
            environment: environment
        ]
        renderInertia 'About/Index', [appInfo: appInfo]
    }

}