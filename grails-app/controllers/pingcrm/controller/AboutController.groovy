package pingcrm.controller

import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import pingcrm.config.AppInfo
import pingcrm.config.GrailsInfo

@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class AboutController {

    private final AppInfo appInfo
    private final GrailsInfo grailsInfo
    private final String groovyVersion
    private final String javaVersion

    @Inject
    AboutController(AppInfo appInfo, GrailsInfo grailsInfo) {
        this.appInfo = appInfo
        this.grailsInfo = grailsInfo
        this.groovyVersion = GroovySystem.version
        this.javaVersion = System.getProperty 'java.version'
    }

    def index() {
        def info = [
            name: appInfo.name,
            version: appInfo.version,
            grailsVersion: appInfo.grailsVersion,
            grailsProfile: grailsInfo.profile,
            grailsEnvironment: grailsInfo.env,
            groovyVersion: groovyVersion,
            javaVersion: javaVersion
        ]
        renderInertia 'About/Index', [info: info]
    }
}