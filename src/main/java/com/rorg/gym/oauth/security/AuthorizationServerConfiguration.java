package com.rorg.gym.oauth.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

@RefreshScope
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
	
	private static final String SCOP_READ 			= "read";
	private static final String SCOP_WRITE 			= "write";
	
	private static final String GRANT_PASSWORD 		= "password";
	private static final String GRANT_REFRESHTOKEN 	= "refresh_token";
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private AditionalInfoToken aditionalInfoToken;
	@Autowired
	private Environment enviroment;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security
			.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()");
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
			.inMemory()
			.withClient(enviroment.getProperty("configuration.security.oauth.clientAngular.id"))
			.secret(bCryptPasswordEncoder.encode(enviroment.getProperty("configuration.security.oauth.clientAngular.secret")))
			.scopes(SCOP_READ,SCOP_WRITE)
			.authorizedGrantTypes(GRANT_PASSWORD,GRANT_REFRESHTOKEN)
			.accessTokenValiditySeconds(Integer.parseInt(enviroment.getProperty("configuration.security.oauth.accessToken.validitySeconds")))
			.refreshTokenValiditySeconds(Integer.parseInt(enviroment.getProperty("configuration.security.oauth.refreshToken.validitySeconds")))
			;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain
			.setTokenEnhancers(Arrays.asList(aditionalInfoToken,accessTokenConverter()));
		endpoints
			.authenticationManager(authenticationManager)
			.accessTokenConverter(accessTokenConverter())
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhancerChain);
	}
	
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey(enviroment.getProperty("configuration.security.oauth.jwt.key"));
		return jwtAccessTokenConverter;
	}

	public AuthorizationServerConfiguration() {
	}
}
