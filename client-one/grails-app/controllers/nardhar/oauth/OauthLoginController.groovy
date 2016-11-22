package nardhar.oauth

import grails.converters.JSON
import uk.co.desirableobjects.oauth.scribe.OauthService

class OauthLoginController {

    OauthService oauthService

    def index() { }

    def success() {
        // the 'server' is the the name of the current oauth2 provider
        def tokenServer = session[oauthService.findSessionKeyForAccessToken('server')]
        def result = oauthService.getServerResource(
            tokenServer,
            'http://localhost:8100/server/profile'
        ).body
        def resultJson = JSON.parse(result) as LinkedHashMap
        render resultJson as JSON
    }

}