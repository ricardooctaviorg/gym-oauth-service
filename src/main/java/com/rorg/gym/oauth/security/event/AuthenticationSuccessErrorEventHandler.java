package com.rorg.gym.oauth.security.event;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.rorg.gym.commons.domain.user.User;
import com.rorg.gym.oauth.service.IUserService;

import feign.FeignException;

@Component
public class AuthenticationSuccessErrorEventHandler implements AuthenticationEventPublisher {

	private Logger logger = LoggerFactory.getLogger(AuthenticationSuccessErrorEventHandler.class);

	private static final String ANGULARAPP = "AngularApp";
	
	
	@Autowired
	private IUserService iUserService;

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		//TODO: Doble Execution 
		Object principal = authentication.getPrincipal();
		 if (principal instanceof UserDetails) {
		        UserDetails details = (UserDetails) principal;
		        logger.info(String.format("%s logged in", details.getUsername()));
		        if (!ANGULARAPP.equals(details.getUsername())) {
		    		User user = iUserService.findUserByUserName(authentication.getName());
		    		if (user.getAttempt() > 0) {
		    			user.setAttempt(0);
		    			iUserService.update(user, user.getIdUser());
		    		}
		    		user.setLastAccess(new Date());
		    		iUserService.update(user, user.getIdUser());
		        }
		    } else 
		        logger.info(principal.toString());
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		logger.error("Error Login: " + exception.getMessage());

		try {
			User user = iUserService.findUserByUserName(authentication.getName());
			user.setAttempt(user.getAttempt() + 1);
			if (user.getAttempt() >= 3)
				user.setLocked(1);
			iUserService.update(user, user.getIdUser());
		} catch (FeignException fE) {
			logger.error(String.format("The user not exist %s", authentication.getName()));
		}
	}

}
