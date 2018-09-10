package learnspringcloud.eurekaclient.user.controller;

import learnspringcloud.eurekaclient.user.domain.User;
import learnspringcloud.eurekaclient.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * @author chenmingming
 * @date 2018/9/10
 */
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/user")
    public User saveUser(String name){
        User user = new User();
        user.setName(name);
        boolean save = userService.save(user);
        if(save){
            return user;
        }else {
            return null;
        }
    }

    @GetMapping("/user/list")
    public Collection<User> finaAll(){
        return userService.findAll();
    }
}
