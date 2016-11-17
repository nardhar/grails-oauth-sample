package nardhar.oauth

import grails.transaction.Transactional

@Transactional
class LdapCheckService {

    def passwordEncoder

    User check(params) {
        def username = params.username
        def password = params.password
        /* def adAuth = new ActiveDirectoryAuthentication('ypfb.gov.bo')
        if (!adAuth.authenticate(username, password)) {
            return null
        } */
        def userRole = Role.findByAuthority('ROLE_USER')
        // guarda el username y el password hasheado
        // verifica si existe para sobreescribir o crear uno nuevo
        def user = User.findByUsername(username)
        if (user) {
            user.password = password
            user.save()
        } else {
            user = new User(username, password).save()
            UserRole.create user, userRole, true
        }
        return user
    }

    User checkDao(params) {
        def username = params.username
        def password = params.password
        def user = User.findByUsername(username)
        if (!passwordEncoder.isPasswordValid(user?.password, password, null)) {
            return null
        }
        return user
    }

}