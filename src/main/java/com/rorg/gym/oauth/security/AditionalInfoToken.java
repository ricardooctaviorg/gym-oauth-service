package com.rorg.gym.oauth.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.rorg.gym.commons.domain.user.User;
import com.rorg.gym.oauth.service.IUserService;

@Component
public class AditionalInfoToken implements TokenEnhancer {
	
	private static final String KEY_IDACCOUNT 			= "idAccount";
	private static final String KEY_IDUSER 				= "idUser";
	
	@Autowired
	private IUserService iUsuarioService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> info 	= new HashMap<String, Object>();
		User user 					= iUsuarioService.findUserByUserName(authentication.getName());
		info.put(KEY_IDACCOUNT, user.getIdAccount().getIdAccount());
		info.put(KEY_IDUSER, user.getIdUser());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		return accessToken;
	}
}
