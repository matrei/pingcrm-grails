package pingcrm

import java.time.LocalDate

import spock.lang.Specification

class PublicDataMapperSpec extends Specification {

    private final PublicDataMapper mapper = new PublicDataMapper()

    void 'only the given properties are selected'() {

        given: 'an object with more properties than selected'
            def person = new Person(name: 'Jane Doe', email: 'jane@example.com', secret: 'secret')

        expect: 'only the selected properties are mapped'
            mapper.map(person, ['name', 'email']) == [name: 'Jane Doe', email: 'jane@example.com']
    }

    void 'nested properties are selected with dot notation'() {

        given: 'an object with an association'
            def person = new Person(name: 'Jane Doe', company: new Company(id: 1, name: 'Acme', secret: 'secret'))

        expect: 'the nested properties are mapped to a nested map'
            mapper.map(person, ['name', 'company.id', 'company.name']) == [name: 'Jane Doe', company: [id: 1, name: 'Acme']]
    }

    void 'a missing association is mapped to null'() {

        given: 'an object without an association'
            def person = new Person(name: 'Jane Doe')

        expect: 'the association is mapped to null'
            mapper.map(person, ['name', 'company.name']) == [name: 'Jane Doe', company: null]
    }

    void 'selecting an associated object as a whole is not allowed'() {

        given: 'an object with an association'
            def person = new Person(name: 'Jane Doe', company: new Company(id: 1, name: 'Acme', secret: 'secret'))

        when: 'selecting the association instead of its properties'
            mapper.map(person, ['name', 'company'])

        then: 'an exception tells to select nested properties instead'
            def e = thrown(IllegalArgumentException)
            e.message == "Property 'company' of Person is not a simple value. Select its nested properties instead, e.g. 'company.id'."
    }

    void 'simple values of different types are mapped'() {

        given: 'an object with properties of different simple types'
            def person = new Person(name: 'Jane Doe', active: true, role: Role.OWNER, birthDate: LocalDate.of(2000, 1, 2))

        expect: 'the values are mapped as they are'
            mapper.map(person, ['name', 'active', 'role', 'birthDate']) ==
                    [name: 'Jane Doe', active: true, role: Role.OWNER, birthDate: LocalDate.of(2000, 1, 2)]
    }

    void 'lists of objects are mapped'() {

        given: 'a list of objects'
            def people = [new Person(name: 'Jane Doe'), new Person(name: 'John Doe')]

        expect: 'each object is mapped'
            mapper.map(people as List<PublicData>, ['name']) == [[name: 'Jane Doe'], [name: 'John Doe']]
    }

    private static class Person implements PublicData {

        String name
        String email
        String secret
        boolean active
        Role role
        LocalDate birthDate
        Company company
    }

    private static enum Role {

        USER, OWNER
    }

    private static class Company {

        Long id
        String name
        String secret
    }
}
