/*
 * Copyright 2022-2024 original authors
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
import pingcrm.config.GitInfo

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
    AboutController(AppInfo appInfo, GitInfo gitInfo,
                    GrailsApplication grailsApplication,
                    GrailsPluginManager pluginManager)
    {
        info.with {
            name = appInfo.name
            version = appInfo.version
            grailsVersion = appInfo.grailsVersion
            grailsEnvironment = Environment.current.name
            groovyVersion = GroovySystem.version
            javaVersion = System.getProperty('java.version')
            ssrEnabled = grailsApplication.config.getProperty('inertia.ssr.enabled', Boolean, false)
            plugins = pluginManager.allPlugins.collect({[name: it.name, version: it.version]}).sort({ it.name })
            numControllers = grailsApplication.getArtefacts('Controller').size()
            numDomains = grailsApplication.getArtefacts('Domain').size()
            numServices = grailsApplication.getArtefacts('Service').size()
            numTagLibs = grailsApplication.getArtefacts('TagLib').size()
        }
        info['git.commitSha'] = gitInfo.commit?.get('id.abbrev')
        info['git.commitUrl'] = "${appInfo.repoUrl}/commit/${gitInfo.commit?.get('id.abbrev')}"
        info['git.commitTime'] = gitInfo.commit?.get('time')
    }

    def index() {
        renderInertia('About/Index', [info: info])
    }
}