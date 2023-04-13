/*
 * Copyright 2022-2023 original authors
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
package pingcrm.controller

import grails.core.GrailsApplication
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.GrailsPluginManager
import grails.util.Environment
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import pingcrm.config.AppInfo
import pingcrm.config.GrailsInfo

/**
 * A controller that renders the about page.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class AboutController {

    static final Map allowedMethods = [index: 'GET']

    private final Map info = [:]

    @Inject
    AboutController(AppInfo appInfo,
                    GrailsInfo grailsInfo,
                    GrailsApplication grailsApplication,
                    GrailsPluginManager pluginManager)
    {
        info.with {
            name = appInfo.name
            version = appInfo.version
            grailsVersion = appInfo.grailsVersion
            grailsProfile = grailsInfo.profile
            grailsEnvironment = Environment.current.name
            groovyVersion = GroovySystem.version
            javaVersion = System.getProperty 'java.version'
            plugins = pluginManager.allPlugins.collect {[name: it.name, version: it.version] }.sort { it.name }
            numControllers = grailsApplication.getArtefacts('Controller').size()
            numDomains = grailsApplication.getArtefacts('Domain').size()
            numServices = grailsApplication.getArtefacts('Service').size()
            numTagLibs = grailsApplication.getArtefacts('TagLib').size()
        }
    }

    def index() {
        renderInertia 'About/Index', [info: info]
    }
}