package nardhar.oauth

import grails.converters.JSON

class OauthLoginController {

    def oauthService

    def index() { }

    def success() {
        def result = oauthService.getServerResource('http://localhost:8100/server/profile')
        render result as JSON
    }

}