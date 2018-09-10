package learnspringcloud.eurekaclient.user.controller;

import learnspringcloud.eurekaclient.user.domain.User;
import learnspringcloud.eurekaclient.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * @author chenmingming
 * @date 2018/9/10
 */
@RestController
public class UserServiceProviderApiController {
    @Autowired
    UserService userService;

    @PostMapping("/user")
    public User saveUser(@RequestBody User user){
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
