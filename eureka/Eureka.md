#  Eureka 服务器

##   服务端配置

* 取消自我注册

```yaml
### Eureka port
server:
  port: 8761

spring:
  application:
  	### 服务名称
    name: spring-cloud-eureka-server

eureka:
  client:
    ### 没有必要去再检索服务
    fetch-registry: false
    ### 关闭自我注册
    register-with-eureka: false
    ### Eureka Server 用于客户端注册
    service-url:
      defaultZone: http://localhost:${server.port}/eureka
```

* Eureka 服务端一般不需要自我注册，也不需要注册其他的服务器
* Eureka 自我注册问题，因为服务器本身还未启动

* 这两项配置不关闭并不影响服务启动，建议关闭以减少不必要的异常信息

