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

import pingcrm.User
import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.codehaus.groovy.util.HashCodeHelper
import grails.compiler.GrailsCompileStatic

/**
 *  A UserRole is a Many-To-Many mapping between Users and Roles.
 *  (started from template by Grails Spring Security Plugin)
 *
 *  @author Mattias Reichel
 *  @since 1.0.0
 */
@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class UserRole implements Serializable {

	@SuppressWarnings('unused')
	private static final long serialVersionUID = 1

	User user
	Role role

	static final constraints = {
		user nullable: false
		role nullable: false, validator: { Role r, UserRole ur ->
			if (ur.user?.id) {
				if (exists ur.user.id, r.id) {
					return ['userRole.exists']
				}
			}
		}
	}

	static final mapping = {
		id composite: ['user', 'role']
		version false
	}

	static Set<Role> findAllRolesForUser(User u) {
		def userRoles = createCriteria().list { eq 'user', u } as Set<UserRole>
		userRoles*.role as Set<Role>
	}

	static UserRole get(User user, Role role) {
		get user.id, role.id
	}

	static UserRole get(Serializable userId, Serializable roleId) {
		criteriaFor(userId, roleId).get()
	}

	static boolean exists(Serializable userId, Serializable roleId) {
		criteriaFor(userId, roleId).count()
	}

	private static DetachedCriteria<UserRole> criteriaFor(Serializable userId, Serializable roleId) {

		where {
			user == User.load(userId) &&
			role == Role.load(roleId)
		}
	}

	static UserRole create(User user, Role role, boolean flush = false) {

		def instance = new UserRole(user: user, role: role)
		instance.save flush: flush

		instance
	}

	@SuppressWarnings('unused')
	static boolean remove(User u, Role r) {

		if (u != null && r != null) {
			return where { user == u && role == r }.deleteAll()
		}

		false
	}

	@SuppressWarnings('unused')
	static int removeAll(User u) {
		u == null ? 0 : where { user == u }.deleteAll() as int
	}

	@SuppressWarnings('unused')
	static int removeAll(Role r) {
		r == null ? 0 : where { role == r }.deleteAll() as int
	}

	@Override
	boolean equals(other) {

		if (other instanceof UserRole) {
			return other.userId == user?.id && other.roleId == role?.id
		}

		false
	}

	@Override
	int hashCode() {

		int hashCode = HashCodeHelper.initHash()

		if (user) hashCode = HashCodeHelper.updateHash hashCode, user.id
		if (role) hashCode = HashCodeHelper.updateHash hashCode, role.id

		hashCode
	}
}
