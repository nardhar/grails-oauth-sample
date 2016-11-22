package server.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore

import javax.sql.DataSource

@Configuration
class OAuth2ServerConfiguration {
    // NOTE: all client_details records must have a resource_ids field with this value
    //       so it generates a token with this resource_id and it does not generates
    //       the "Invalid token does not contain resource id (SERVERAPI)" error
    static final String RESOURCE_ID = 'SERVERAPI'

    @Configuration
    @EnableResourceServer
    @EnableGlobalMethodSecurity(jsr250Enabled = true)
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
        
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(RESOURCE_ID)
        }
        
        @Override
        @Order(3) // establishing a corresponding order so it configures it AFTER the WebSecurityServer
        public void configure(HttpSecurity http) throws Exception {
            // configure the httpSecurity so it only enables the /profile route as a resource
            // and only if it complies the "#oauth2.hasScope('read')" filter
            http.requestMatchers()
                    .antMatchers("/profile", "/profile/**")
                .and()
                    .authorizeRequests()
                    .anyRequest().access("#oauth2.hasScope('read')")
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private DataSource dataSource

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager

        @Autowired
        private CustomUserDetailsService userDetailsService

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                .tokenStore(tokenStore())
                .authenticationManager(this.authenticationManager)
                .userDetailsService(userDetailsService)
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.jdbc(dataSource)
        }
        

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
        }

        @Bean
        @Primary
        public DefaultTokenServices tokenServices() {
            DefaultTokenServices tokenServices = new DefaultTokenServices()
            tokenServices.setSupportRefreshToken(true)
            tokenServices.setTokenStore(tokenStore())
            return tokenServices
        }

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource)
        }
    }
}