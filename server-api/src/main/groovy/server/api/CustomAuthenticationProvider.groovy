package server.api

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.GrantedAuthority

class CustomAuthenticationProvider implements AuthenticationProvider {

    def grailsApplication
    def personCheckService

    Authentication authenticate(Authentication customAuth) {
        // TODO: Si es usuario interno (customAuth.principal.enterprise == null || customAuth.principal.enterprise == 'YPFB')
        //       Consulta con el AD y en caso de no haber conexion consulta en BD con el ultimo pass hasheado satisfactorio
        //       Si es usuario externo (customAuth.principal.enterprise != null && customAuth.principal.enterprise != 'YPFB')
        //       Consulta en BD con el pass enviado
        List<GrantedAuthority> grantedAuths
        Person user
        // verifica si fuera usuario interno/externo
        if ((grailsApplication.config.ldap?.domainMails ?: []).any { customAuth.principal.endsWith('@' + it) } ||
            !customAuth.principal.contains('@')
        ) {
            // corta el dominio del correo
            // porque asume que el usuario del dominio es el mismo que el correo
            def username = customAuth.principal.contains('@') ?
                           customAuth.principal.substring(0, customAuth.principal.indexOf('@')) :
                           customAuth.principal
            user = personCheckService.check(
                username: username,
                password: customAuth.credentials
            )
        } else { // si fuera usuario externo
            user = personCheckService.checkDao(
                username: customAuth.principal,
                password: customAuth.credentials
            )
        }
        if (user) {
            Person.withTransaction { status ->
                Collection<SecurityRole> userAuthorities = user.securityRoles
                grantedAuths = userAuthorities.collect { new SimpleGrantedAuthority(it.authority) }
            }
            /* def userDetails = new GrailsUser(user.username, user.password, user.enabled,
                !user.accountExpired, !user.passwordExpired, !user.accountLocked,
                grantedAuths, user.id
            ) */
            return new UsernamePasswordAuthenticationToken(user.username, user.password, grantedAuths)
        } else {
            throw new BadCredentialsException("Log in failed - identity could not be verified")
        }
    }

    boolean supports(Class<? extends Object> aClass) {
        return true
    }

}