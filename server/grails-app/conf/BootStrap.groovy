import nardhar.oauth.Role
import nardhar.oauth.Client
import nardhar.oauth.User
import nardhar.oauth.UserRole

class BootStrap {

    def init = { servletContext ->
        def userRole = new Role('ROLE_USER').save()
        def userClient = new Role('ROLE_CLIENT').save()
        //def clientSample = new Role('ROLE_CLIENT').save()
        new Client(
            clientId: 'my-client-one',
            clientSecret: 'my-secret-one',
            authorizedGrantTypes: ['authorization_code', 'refresh_token', 'implicit', 'password', 'client_credentials'],
            authorities: ['ROLE_CLIENT'],
            scopes: ['read','write'],
            //autoApproveScopes: ['true', 'read'],
            redirectUris: ['http://localhost:8110/client-one/oauthLogin/server']
        ).save(flush: true)
        new Client(
            clientId: 'my-client-two',
            clientSecret: 'my-secret-two',
            authorizedGrantTypes: ['authorization_code', 'refresh_token', 'implicit', 'password', 'client_credentials'],
            authorities: ['ROLE_CLIENT'],
            scopes: ['read','write'],
            //autoApproveScopes: ['true', 'read'],
            redirectUris: ['http://localhost:8120/client-two/oauthLogin/server']
        ).save(flush: true)
        // external user
        // guarda el username y el password hasheado
        // verifica si existe para sobreescribir o crear uno nuevo
        def myUser1 = new User('myuser1@demo.com', 'mypass1', 'my user number one')
        myUser1.save()
        UserRole.create myUser1, userRole, true
        def myUser2 = new User('myuser2@demo.com', 'mypass2', 'my user number two')
        myUser2.save()
        UserRole.create myUser2, userRole, true
    }
    def destroy = {
    }
}
