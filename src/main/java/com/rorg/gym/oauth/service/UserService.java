package com.rorg.gym.oauth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rorg.gym.commons.domain.core.rolePolicy.PolicySimple;
import com.rorg.gym.commons.domain.core.rolePolicy.Role;
import com.rorg.gym.commons.domain.user.User;
import com.rorg.gym.commons.domain.user.UserPassword;
import com.rorg.gym.oauth.clients.UserFeingClient;

import feign.FeignException;

@Service
public class UserService implements UserDetailsService, IUserService {

	private Logger log 					= LoggerFactory.getLogger(UserService.class);
	
	private static final Integer ONE 	= 1;

	@Autowired
	UserFeingClient userFeingClient;

	public UserService() {
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			User user 					= userFeingClient.findUserByUserName(username);
			UserPassword findByIdUser 	= userFeingClient.findUserPasswordByIdUser(user.getIdUser());
			List<Role> listRole 		= new ArrayList<Role>();
			Role role 					= user.getIdRole();
			
			listRole.add(role);
			
			List<PolicySimple> policys = role.getPolicys();
			log.debug("policys.toString(): " + policys.toString());

			return new org.springframework.security.core.userdetails.User(username
					, findByIdUser.getPasswordHash()
					, ONE.equals(user.getStatus()) ? true : false
					, true
					, true
					, true	
					, getAuthorities(listRole));
		} catch (FeignException e) {
			log.error("Error::loadUserByUsername()");
			throw new UsernameNotFoundException("Error::loadUserByUsername()");
		}
	}

	// https://www.baeldung.com/spring-security-granted-authority-vs-role
	private Collection<? extends GrantedAuthority> getAuthorities(List<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getNameRole()));
		}
		return authorities;
	}

	@Override
	public User findUserByUserName(String userName) {
		return userFeingClient.findUserByUserName(userName);
	}

	@Override
	public User update(User user, Long idUser) {
		return userFeingClient.update(user, idUser);
	}

}
