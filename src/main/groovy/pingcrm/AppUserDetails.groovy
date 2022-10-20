package pingcrm

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority

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
        Collection< GrantedAuthority> authorities,
        Object id,
        String fullName
    )
    {
        super(email, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id)
        this.fullName = fullName
    }
}
