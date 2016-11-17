package nardhar.oauth

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.GrantedAuthority
import grails.plugin.springsecurity.userdetails.GrailsUser

class CustomAuthenticationProvider implements AuthenticationProvider {

    def ldapCheckService

    Authentication authenticate(Authentication customAuth) {
        // TODO: Si es usuario interno (customAuth.principal.enterprise == null || customAuth.principal.enterprise == 'YPFB')
        //       Consulta con el AD y en caso de no haber conexion consulta en BD con el ultimo pass hasheado satisfactorio
        //       Si es usuario externo (customAuth.principal.enterprise != null && customAuth.principal.enterprise != 'YPFB')
        //       Consulta en BD con el pass enviado
        List<GrantedAuthority> grantedAuths
        User user
        // verifica si fuera usuario interno/externo
        if (customAuth.principal.endsWith('@ypfb.gob.bo') || !customAuth.principal.contains('@')) {
            def username = customAuth.principal.endsWith('@ypfb.gob.bo') ?
                           customAuth.principal.substring(0, customAuth.principal.indexOf('@')) :
                           customAuth.principal
            user = ldapCheckService.check(
                username: username,
                password: customAuth.credentials
            )
        } else { // si fuera usuario externo
            user = ldapCheckService.checkDao(
                username: customAuth.principal,
                password: customAuth.credentials
            )
        }
        if (user) {
            User.withTransaction { status ->
                grantedAuths = user.getAuthorities().collect {
                    new GrantedAuthorityImpl(it.authority)
                }
            }
            def userDetails = new GrailsUser(user.username, user.password, user.enabled,
                !user.accountExpired, !user.passwordExpired, !user.accountLocked,
                grantedAuths, user.id
            )
            return new UsernamePasswordAuthenticationToken(userDetails, user.password, grantedAuths)
        } else {
            throw new BadCredentialsException("Log in failed - identity could not be verified")
        }
    }

    boolean supports(Class<? extends Object> aClass) {
        return true
    }

}