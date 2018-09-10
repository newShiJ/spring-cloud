package learnspringcloud.eurekaclient.user.service;

import learnspringcloud.eurekaclient.user.domain.User;

import java.util.Collection;

/**
 * @author chenmingming
 * @date 2018/9/10
 */
public interface UserService {

    /**
     * 保存用户
     * @param user
     * @return
     */
    boolean save(User user);

    /**
     * 查询所有
     * @return 不会返回 <code>null</code>
     */
    Collection<User> findAll();
}
