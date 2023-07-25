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
package pingcrm.auth

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

/**
 * A role domain class (that can be assigned to a user).
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@GrailsCompileStatic
@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class Role implements Serializable {

	@SuppressWarnings('unused')
	private static final long serialVersionUID = 1

	String authority

	static final constraints = { authority nullable: false, blank: false, unique: true }
	static final mapping = { cache true }

	static Role findByAuthority(String auth) {
		createCriteria().get {
			eq 'authority', auth
		} as Role
	}
}
