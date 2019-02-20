package com.unsc.shard.web.api;

import com.unsc.shard.bean.User;
import com.unsc.shard.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/user/save")
	@ResponseBody
	public String save(User user) {
		userService.save(user);
		return "success";
	}
	
	@RequestMapping("/user/get/{id}")
	@ResponseBody
	public User get(@PathVariable Long id) {
		User user =  userService.get(id);
		System.out.println(user.getId());
		return user;
	}

    @RequestMapping("/user/press/operate")
    @ResponseBody
    public String pressOperateTest(@RequestBody User user) {
	    log.info("User: {}", user);
	    int number = 4;
        List<User> list = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            list.add(new User("黄狗", "100", "84432@qq.com", "Fuck", 31000, 2));
        }
        return userService.mockOrderOperateWithUser(user, list);
    }
}
