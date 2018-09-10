package learnspringcloud.eurekaclient.user.service;

import learnspringcloud.eurekaclient.user.domain.User;
import learnspringcloud.eurekaclient.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author chenmingming
 * @date 2018/9/10
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @Override
    public boolean save(User user) {
        return userRepo.save(user);
    }

    /**
     * 查询所有
     *
     * @return 不会返回 <code>null</code>
     */
    @Override
    public Collection<User> findAll() {
        return userRepo.findAll();
    }
}
