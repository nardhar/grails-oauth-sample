package server.api

import org.springframework.security.core.context.SecurityContextHolder
import javax.annotation.security.RolesAllowed
import static org.springframework.http.HttpStatus.*
import static org.springframework.http.HttpMethod.*

class ProfileController {

    def index() {
        // should render profile info per scope and be rest
        // we can use the principal to get the User info as the username is unique
        def personInstance = Person.findByUsername(SecurityContextHolder.context?.authentication?.principal)
        
        respond personInstance
    }
}