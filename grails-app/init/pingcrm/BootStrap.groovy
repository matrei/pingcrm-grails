package pingcrm

import net.datafaker.Faker
import pingcrm.auth.Role
import pingcrm.auth.UserRole

import javax.transaction.Transactional
import java.time.Instant

class BootStrap {

    def init = { servletContext ->
        seedDatabase()
    }

    @Transactional
    void seedDatabase() {

        // Create user roles
        def ownerRole = new Role(authority: 'ROLE_OWNER').save failOnError: true
        def userRole = new Role(authority: 'ROLE_USER').save failOnError: true

        // Create the "owner" account
        def account = new Account(name: 'Acme Corporation').save failOnError: true
        def owner = new User(
                account: account,
                firstName: 'John',
                lastName: 'Doe',
                email: 'johndoe@example.com',
                password: 'secret',
                owner: true
        ).save failOnError: true
        UserRole.create owner, ownerRole

        // Create all the user accounts
        Faker faker = new Faker(new Locale('en-US'))
        5.times {
            def user = new User(
                    account: account,
                    firstName: faker.name().firstName(),
                    lastName: faker.name().lastName(),
                    email: faker.internet().safeEmailAddress(),
                    emailVerifiedAt: Instant.now(),
                    password: faker.internet().password(),
                    owner: false
            ).save failOnError: true
            UserRole.create user, userRole
        }

        // Create all the organisations
        def organizations = []
        100.times {
            def organization = new Organization(
                    account: account,
                    name: faker.company().name(),
                    email: faker.internet().safeEmailAddress(),
                    phone: faker.phoneNumber().phoneNumber(),
                    address: faker.address().streetAddress(),
                    city: faker.address().city(),
                    region: faker.address().state(),
                    country: 'US',
                    postalCode: faker.address().zipCode()
            ).save failOnError: true
            organizations << organization
        }

        // Create all the contacts
        Random random = new Random()
        100.times {
            new Contact(
                    account: account,
                    organization: organizations.get(random.nextInt(100)),
                    firstName: faker.name().firstName(),
                    lastName: faker.name().lastName(),
                    email: faker.internet().safeEmailAddress(),
                    phone: faker.phoneNumber().cellPhone(),
                    address: faker.address().streetAddress(),
                    city: faker.address().city(),
                    region: faker.address().state(),
                    country: 'US',
                    postalCode: faker.address().zipCode()
            ).save failOnError: true
        }
    }
}
