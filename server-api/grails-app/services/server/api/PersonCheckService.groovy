package server.api

import grails.transaction.Transactional

@Transactional
class PersonCheckService {

    def grailsApplication
    def ldapUserService
    def passwordEncoder

    Person check(params) {
        def username = params.username
        def password = params.password
        def ldapUserInstance = ldapUserService.check(username: username, password: password)
        if (!ldapUserInstance) {
            return null
        }
        // todos los usuarios serian ROLE_CLIENT
        def userRole = SecurityRole.findByAuthority(grailsApplication.config.ldap?.userRole ?: 'ROLE_CLIENT')
        // guarda el username y el password hasheado
        // verifica si existe para sobreescribir o crear uno nuevo
        def user = Person.findByUsername(username)
        if (user) {
            user.password = password
            user.save()
        } else {
            // should check the fullname and the email
            user = new Person(
                username: username,
                password: password,
                fullName: ldapUserInstance.attributes?.getAt(grailsApplication.config.ldap?.userFullNameAttribute ?: 'fullName'),
                email: ldapUserInstance.attributes?.getAt(grailsApplication.config.ldap?.userEmailAttribute ?: 'email')
            )
            if (!user.save()) {
                user.errors.allErrors.each { log.info "field: ${it.field}, code: ${it.code}, ${it.arguments}" }
            }
            PersonSecurityRole.create user, userRole, true
        }
        return user
    }

    Person checkDao(params) {
        def username = params.username
        def password = params.password
        def user = Person.findByUsername(username)
        if (!user) {
            return null
        }
        if (!passwordEncoder.isPasswordValid(user?.password, password, null)) {
            return null
        }
        return user
    }

}