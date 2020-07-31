package com.rorg.gym.oauth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.rorg.gym.commons.domain.user.User;
import com.rorg.gym.commons.domain.user.UserPassword;


@FeignClient(name = "gym-user-service")
public interface UserFeingClient {
	
	@GetMapping("/user/search/findByUserName")
	public User findUserByUserName(@RequestParam String userName);
	
	@GetMapping("/userPassword/search/findByIdUser")
	public UserPassword findUserPasswordByIdUser(@RequestParam Long idUser);
		
	@PutMapping("/user/{idUser}")
	public User update(@RequestBody User user, @PathVariable Long idUser);
}
