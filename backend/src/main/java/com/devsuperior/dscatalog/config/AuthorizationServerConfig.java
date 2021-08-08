package com.devsuperior.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.devsuperior.dscatalog.components.JwtTokenEnhacer;

@Configuration
@EnableAuthorizationServer // sets this class as OAuth autorization server
public class AuthorizationServerConfig
		extends AuthorizationServerConfigurerAdapter {
	
	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	@Value("${security.oauth2.client.client-secret}")	
	private String clientSecret;
	@Value("${jwt.duration}")
	private Integer jwtDuration;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	@Autowired
	private JwtTokenStore tokenStore;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenEnhacer tokenEnhancer;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security)
			throws Exception {
		security.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients)
			throws Exception {
		clients.inMemory()
				.withClient(clientId) // chosen name - to me moved to configuration
				.secret(passwordEncoder.encode(clientSecret)) // temporary hardcode - to me moved to configuration
				.scopes("read", "write")
				.authorizedGrantTypes("password")
				.accessTokenValiditySeconds(jwtDuration);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws Exception {
		TokenEnhancerChain chain = new TokenEnhancerChain();
		chain.setTokenEnhancers(Arrays.asList(accessTokenConverter, tokenEnhancer) );
		endpoints.authenticationManager(authenticationManager)
				.tokenStore(tokenStore)
				.accessTokenConverter(accessTokenConverter)
				.tokenEnhancer(chain);
	}

}
