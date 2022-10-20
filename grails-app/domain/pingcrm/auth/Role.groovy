package pingcrm.auth

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class Role implements Serializable {

	@SuppressWarnings('unused')
	private static final long serialVersionUID = 1

	String authority

	static constraints = { authority nullable: false, blank: false, unique: true }
	static mapping = { cache true }

	static Role findByAuthority(String auth) {
		createCriteria().get {
			eq'authority', auth
		} as Role
	}
}
