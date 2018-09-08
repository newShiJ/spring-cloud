package learnspring.configclient.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * @author chenmingming
 * @date 2018/9/8
 */
public class MyHealthIndicator extends AbstractHealthIndicator {

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.down().withDetail("MyHealthIndicator", "Go Go Go");
    }
}
