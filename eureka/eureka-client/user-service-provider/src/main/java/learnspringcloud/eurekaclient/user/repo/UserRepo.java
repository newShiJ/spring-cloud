package learnspringcloud.eurekaclient.user.repo;

import learnspringcloud.eurekaclient.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chenmingming
 * @date 2018/9/10
 */
@Repository
public class UserRepo {
    private ConcurrentHashMap<Long,User> repository = new ConcurrentHashMap<>();

    private static final AtomicLong IDS = new AtomicLong(0L);

    public boolean save(User user){
        user.setId(IDS.incrementAndGet());
        return repository.putIfAbsent(user.getId(), user) == null;
    }

    public Collection<User> findAll(){
        return repository.values();
    }
}
