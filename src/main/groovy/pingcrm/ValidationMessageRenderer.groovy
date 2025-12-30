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

import groovy.transform.CompileStatic

import jakarta.inject.Inject

import org.springframework.validation.Errors

import org.grails.plugins.web.taglib.ValidationTagLib

/**
 * A validation message renderer that uses the Grails validation tag library.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
trait ValidationMessageRenderer {

    @Inject
    ValidationTagLib g

    Map renderErrors(Errors errors) {
        errors.fieldErrors.collectEntries {
            [it.field, g.message(error: it)]
        }
    }
}