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

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

import grails.gorm.transactions.Transactional

/**
 * A custom implementation of {@link UserDetailsService} that loads user
 * details from the database.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class AppUserDetailsService implements UserDetailsService {

    /**
     * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least
     * one role, so we give a user with no granted roles this one which gets
     * past that restriction but doesn't grant anything.
     */
    static final List NO_ROLES = [
            new SimpleGrantedAuthority('ROLE_NO_ROLES')
    ]

    @Override
    @Transactional(
            readOnly = true,
            noRollbackFor = [IllegalArgumentException, UsernameNotFoundException]
    )
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var user = User.createCriteria().get {
            ilike('email', username?.toLowerCase(Locale.ENGLISH))
        } as User
        if (!user || user.deleted) throw new UsernameNotFoundException('User not found')

        var roles = user.roles

        // or if you are using role groups:
        // def roles = user.authorities.collect { it.authorities }.flatten().unique()

        var authorities = roles.collect {
            new SimpleGrantedAuthority(it.authority)
        }

        new AppUserDetails(
            user.email,
            user.password,
            user.enabled,
            !user.accountExpired,
            !user.passwordExpired,
            !user.accountLocked,
            (authorities ?: NO_ROLES) as Collection<GrantedAuthority>,
            user.id,
            user.name
        )
    }
}