package learnspring.configclient;

import learnspring.configclient.health.MyHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Set;

@SpringBootApplication
@Component
public class ConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

    private final ContextRefresher contextRefresher;

    private final Environment environment;
    @Autowired
    public ConfigClientApplication(ContextRefresher contextRefresher, Environment environment) {
        this.contextRefresher = contextRefresher;
        this.environment = environment;
    }

    //@Scheduled(fixedRate = 5 * 1000, initialDelay = 3 * 1000)
    public void autoRefresh() {
        Set<String> refresh = contextRefresher.refresh();
        for (String name : refresh) {
            String format = String.format("Thread:%s 配置刷新 key:%s => value:%s",
                    Thread.currentThread().getName(),
                    name,
                    environment.getProperty(name));
            System.err.println(format);
        }
    }

    @Bean
    public MyHealthIndicator myHealth(){
        return new MyHealthIndicator();
    }
}
