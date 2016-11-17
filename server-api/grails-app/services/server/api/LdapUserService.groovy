package server.api

import grails.transaction.Transactional
import javax.naming.AuthenticationException
import javax.naming.Context
import javax.naming.NamingEnumeration
import javax.naming.NamingException
import javax.naming.directory.Attributes
import javax.naming.directory.SearchControls
import javax.naming.directory.SearchResult
import javax.naming.ldap.InitialLdapContext
import javax.naming.ldap.LdapContext

@Transactional
class LdapUserService {

    def grailsApplication

    LdapUser check(params) {
        def username = params.username
        def password = params.password
        def ldapUserInstance = authenticate(username, password)
        if (ldapUserInstance.hasErrors()) {
            return null
        }
        return ldapUserInstance
    }

    private LdapUser authenticate(String username, String password) {
        def ldapUserInstance = new LdapUser(
            username: username,
            password: password
        )
        try {
            //Connect with ldap
            LdapContext ldapContextInstance = new InitialLdapContext(
                buildContextProperties(ldapUserInstance.username, ldapUserInstance.password),
                null
            )
            // get attributes
            ldapUserInstance.attributes = getAttributes(ldapContextInstance, username)
        } catch (AuthenticationException e) {
            //Connection failed
            log.info "Connection failed: " + e.message
            ldapUserInstance.errors.rejectValue('password', 'ldapUser.password.invalid.error')
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            return ldapUserInstance
        }
    }

    Hashtable<String, String> buildContextProperties(String username, String password) {
        String principal = grailsApplication.config.ldap?.principalPrefix ||
                           grailsApplication.config.ldap?.principalSuffix ?
                           "${grailsApplication.config.ldap?.principalPrefix ?: ''}${username}${grailsApplication.config.ldap?.principalSuffix ?: ''}".toString() :
                           username
        
        Hashtable<String, String> env1 = new Hashtable<String, String>()
        env1.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
        env1.put(Context.SECURITY_AUTHENTICATION, grailsApplication.config.ldap?.authenticationType ?: "simple");
        // modify SECURITY_PRINCIPAL for ldap server flavor (OpenLdap, Windows AD, etc.)
        //env1.put(Context.SECURITY_PRINCIPAL, "uid=${username},ou=People,dc=example,dc=com".toString());
        env1.put(Context.SECURITY_PRINCIPAL, principal);
        env1.put(Context.SECURITY_CREDENTIALS, password);
        env1.put(Context.PROVIDER_URL, grailsApplication.config.ldap?.providerUrl);
        return env1;
    }

    private LinkedHashMap getAttributes(LdapContext ldapContextInstance, String username) {
        LinkedHashMap contextAttributes = [:]
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        List<String> attrIDs = grailsApplication.config.ldap?.attributeIds ?: [
            'displayName',
            'sn',
            'givenName',
            'email',
        ]
        constraints.setReturningAttributes(attrIDs as String[]);
        //First input parameter is search bas, it can be "CN=Users,DC=YourDomain,DC=com"
        //Second Attribute can be uid=username
        NamingEnumeration answer = ldapContextInstance.search(
            grailsApplication.config.ldap?.domainCompany ?: 'ou=People,dc=example,dc=com',
            (grailsApplication.config.ldap?.accountNameProperty ?: 'uid') + '=' + username,
            constraints
        );
        if (answer.hasMore()) {
            Attributes attrs = ((SearchResult) answer.next()).getAttributes();
            //println attrs.getAt('displayName').values
            contextAttributes = attrIDs.inject([:]) { collectedAttrs, attrID ->
                collectedAttrs + [(attrID): attrs.get(attrID).values.getAt(0)]
            }
        }
        return contextAttributes
    }

}