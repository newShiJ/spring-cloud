package learnspring.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * 输出配置项内容
 * @author chenmingming
 * @date 2018/9/7
 */
@RestController
@RefreshScope
public class EchoController {
    @Value("${my.name}")
    private String myName;

    @GetMapping("/myName")
    public String getMyName(){
        return myName;
    }
}
