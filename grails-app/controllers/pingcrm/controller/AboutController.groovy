package pingcrm.controller

import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.GrailsPluginManager
import grails.plugins.PluginManagerAware
import grails.util.Environment
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import pingcrm.config.AppInfo
import pingcrm.config.GrailsInfo

@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class AboutController implements PluginManagerAware {

    private final Map info = [:]

    @Inject
    AboutController(AppInfo appInfo, GrailsInfo grailsInfo) {
        info.with {
            name = appInfo.name
            version = appInfo.version
            grailsVersion = appInfo.grailsVersion
            grailsProfile = grailsInfo.profile
            grailsEnvironment = Environment.current.name
            groovyVersion = GroovySystem.version
            javaVersion = System.getProperty 'java.version'
        }
    }

    def index() {
        renderInertia 'About/Index', [info: info]
    }

    @Override
    void setPluginManager(GrailsPluginManager pluginManager) {
        info.plugins = pluginManager.allPlugins.collect {[name: it.name, version: it.version] }.sort { it.name }
        info.numControllers = grailsApplication.getArtefacts('Controller').size()
        info.numDomains = grailsApplication.getArtefacts('Domain').size()
        info.numServices = grailsApplication.getArtefacts('Service').size()
        info.numTagLibs = grailsApplication.getArtefacts('TagLib').size()
    }
}