package nardhar.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;

public class CustomServerApi extends DefaultApi20 {

    public CustomServerApi() {
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "http://localhost:8100/server/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig var1) {
        return "http://localhost:8100/server/oauth/authorize?response_type=code&client_id=my-client-two&scope=read";
    }
}
