package server.api

import grails.util.Environment

class BootStrap {

    def init = { servletContext ->
        if (true) {
            PersonSecurityRole.executeUpdate("delete PersonSecurityRole")
            SecurityRole.executeUpdate("delete SecurityRole")
            Person.executeUpdate("delete Person")
        }

        def adminRole = SecurityRole.findByAuthority('ROLE_ADMIN') ?: new SecurityRole(authority: 'ROLE_ADMIN').save(failOnError: true)
        def userRole = SecurityRole.findByAuthority('ROLE_CLIENT') ?: new SecurityRole(authority: 'ROLE_CLIENT').save(failOnError: true)

        def user1 = Person.findByUsername('sampleperson') ?: new Person(username: 'sampleperson', email: 'sampleperson@example.com', password: 'xyz', fullName: 'Sample Person').save(failOnError: true)
        if (!user1.securityRoles.contains(userRole)) {
            PersonSecurityRole.create user1, userRole, true
        }
        if (!user1.securityRoles.contains(adminRole)) {
            PersonSecurityRole.create user1, adminRole, true
        }
    }

    def destroy = {
    }
}
