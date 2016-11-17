package server.api

import grails.validation.Validateable

class LdapUser implements Validateable {

    String username
    String password
    LinkedHashMap attributes

    static constraints = {
        username blank: false
        password blank: false
    }

}