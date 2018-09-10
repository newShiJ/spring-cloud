package learnspringcloud.eurekaclient.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author chenmingming
 * @date 2018/9/10
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceProviderBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceProviderBootstrap.class,args);
    }
}
