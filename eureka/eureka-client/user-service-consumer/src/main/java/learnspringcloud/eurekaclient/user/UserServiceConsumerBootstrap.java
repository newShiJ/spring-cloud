package learnspringcloud.eurekaclient.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenmingming
 * @date 2018/9/10
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceConsumerBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceConsumerBootstrap.class,args);
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }
}
