package com.rorg.gym.oauth.service;

import com.rorg.gym.commons.domain.user.User;

public interface IUserService {
	
	public User findUserByUserName(String userName);
	public User update(User user,Long idUser);
}
