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
package pingcrm

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority

/**
 * A custom user details class that adds a full name property.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class AppUserDetails extends GrailsUser {

    final String fullName

    AppUserDetails(
        String email,
        String password,
        boolean enabled,
        boolean accountNonExpired,
        boolean credentialsNonExpired,
        boolean accountNonLocked,
        Collection<GrantedAuthority> authorities,
        Object id,
        String fullName
    )
    {
        super(email, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id)
        this.fullName = fullName
    }
}
