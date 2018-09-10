package learnspringcloud.eurekaclient.user.service;

import learnspringcloud.eurekaclient.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

/**
 * @author chenmingming
 * @date 2018/9/10
 */
@Service
public class UserServiceProxy implements UserService{

    private static final String PREFIX = "http://user-service-provider/";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @Override
    public boolean save(User user) {
        User user1 = restTemplate.postForObject(PREFIX + "/user", user, User.class);
        return user1 != null;
    }

    /**
     * 查询所有
     *
     * @return 不会返回 <code>null</code>
     */
    @Override
    public Collection<User> findAll() {
        Collection forObject = restTemplate.getForObject(PREFIX + "/user/list", Collection.class);
        return forObject;
    }
}
