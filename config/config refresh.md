# Config Server

## 构建 Spring Cloud 配置服务端
### 实现步骤
1. 在启动类上标记 `@EnableConfigServer` 注解

2. 配置文件目录（基于git）
  1. start.properties	(默认) //默认环境，跟着代码仓库
  2. start-dev.properties	（profile=dev） //开发环境
  3. start-test.properties (profile=test) //测试环境
  4. start-prod.properties (profile=prod) //生产环境

3. 服务端 配置版本仓库（本地）

```properties
  spring.cloud.config.server.git.uri=file:/Users/chenmingming/workspace/java/my/config
```
  * spring cloud config server 启动日志 

  ```properties
  //通过观察启动日志可以  以下为日志截取
  Mapped "{[/{name}/{profiles}/{label:.*}],methods=[GET]}" 
  Mapped "{[/{label}/{name}-{profiles}.properties],methods=[GET]}"
  Mapped "{[/{name}-{profiles}.json],methods=[GET]}" 
  Mapped "{[/{label}/{name}-{profiles}.json],methods=[GET]}" 
  Mapped "{[/{label}/{name}-{profiles}.yml || /{label}/{name}-{profiles}.yaml],methods=[GET]}" 
  Mapped "{[/{name}-{profiles}.yml || /{name}-{profiles}.yaml],methods=[GET]}" 
  ```

  ![效果展示](https://image.ibb.co/hqrnKe/1536226997384.jpg)

  * 服务端的完整配置项
  		* 需要关闭安全检查的配置，因为重新加载及服务器器上下文环境一般是不允许外部访问的

  ```properties
  ### 服务器配置项
  server.port=9090
  spring.application.name=config-server
  ### 本地仓库 git URL配置
  #spring.cloud.config.server.git.uri=file:/Users/chenmingming/workspace/java/my/config
  
  ### 配置远程仓库
  spring.cloud.config.server.git.uri=https://gitee.com/hrnd/git-config
  spring.cloud.config.server.git.username=username
  spring.cloud.config.server.git.password=password
  
  ## 关闭 Actuator 安全检查
  #management.security.enabled=false
  
  ## 细粒度开放
  ## 环境 env 打开
  endpoints.env.sensitive=false
  ## 健康检查权限打开
  endpoints.health.sensitive=false
  ```

  * 访问方式

  ```java
  	uri = http://localhost:9090/
  	{application} = start
  	{profile} = dev
  	{suffix} = properties || json || yml
  	
  	URL = uri/{application}-{profile}.{suffix}
  		=> http://localhost:9090/start-dev.yml 
  ```

       

  ## Config Client 配置客户端 

  ==版本采坑经验==     

 SpringBoot `1.5.15.RELEASE` SpringCloud `Edgware.SR4` 下需要将`spring.cloud.config.uri`的配置放在 `bootstrap.properties`中

 ```properties
 # 配置服务器 URI
spring.cloud.config.uri=http://localhost:9090/
 ```

 #### application.properties 配置

 ```properties
 ### 客户端配置项

 server.port=8080
 spring.application.name=config-client
 
 # 配置客户端应用名称 {application}.properties
 spring.cloud.config.name=start
 
 # 环境（dev,test ...）待选择环境
 spring.cloud.config.profile=dev
 
 # label是git指的是分支名称
 spring.cloud.config.label=master
 
 ## 定点开放
 
 ## 健康检查开放
 endpoints.heapdump.sensitive=false
 ## 环境 env 
 endpoints.env.sensitive=false
 ## 上下文中 bean
 endpoints.beans.sensitive=false
 ## 重新刷新上下文
 endpoints.refresh.enabled=false
 ```
 实现效果
 ![实现效果](https://image.ibb.co/jU1wvK/1536240081498.jpg)

 * 可以看出client默认加载了两个配置文件一个是默认的，另外一个是profile的文件

 ### 实时刷新client配置
 观察client 启动的日志
 ![](https://image.ibb.co/hMLvpe/1536240794598.jpg)
==通过访问refresh接口进行客户端配置的刷新==      

![](https://image.ibb.co/iZuc2z/1536241022279.jpg)


![](https://image.ibb.co/mNM1Ue/1536241085096.jpg)

## 动态配置属性Bean

### @RefreshScope
* 利用`@RefreshScope`注解放在需要刷新配置的Bean上当客户端执行刷新动作时会将内部的配置信息进行重新加载
	* 如下代码当客户端刷新配置会将 myName 进行重新加载
	
```java
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
```

### RefreshEndpoint
* spring刷新的机制
  * 首先观察启动日志

```java
  2018-09-07 11:00:34.513  INFO 24924 --- [           main] 
  o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped 
  "{[/refresh || /refresh.json],methods=[POST]}" onto 
  public java.lang.Object 
  org.springframework.cloud.endpoint.GenericPostableMvcEndpoint.invoke()
```
<hr/>

####GenericPostableMvcEndpoint 

  * 进入`GenericPostableMvcEndpoint`看看`invoke()` 代码如下

```java
  public class GenericPostableMvcEndpoint extends EndpointMvcAdapter {
  
  	public GenericPostableMvcEndpoint(Endpoint<?> delegate) {
  		super(delegate);
  	}
  	
  	@RequestMapping(method = RequestMethod.POST)
  	@ResponseBody
  	@Override
  	public Object invoke() {
  		if (!getDelegate().isEnabled()) {
  			return new ResponseEntity<>(Collections.singletonMap(
  					"message", "This endpoint is disabled"), HttpStatus.NOT_FOUND);
  		}
  		return super.invoke();
  	}
  }
```
<hr>

####EndpointMvcAdapter

  * 看看 `GenericPostableMvcEndpoint`父类 `EndpointMvcAdapter`的实现

  ```java
  public class EndpointMvcAdapter extends AbstractEndpointMvcAdapter<Endpoint<?>> {
  
  	/**
  	 * Create a new {@link EndpointMvcAdapter}.
  	 * @param delegate the underlying {@link Endpoint} to adapt.
  	 */
  	public EndpointMvcAdapter(Endpoint<?> delegate) {
  		super(delegate);
  	}
  
  	@Override
  	@ActuatorGetMapping
  	@ResponseBody
  	public Object invoke() {
  		return super.invoke();
  	}
  
  }
  ```
<hr>

#### AbstractEndpointMvcAdapter

  * 再往上一层 `EndpointMvcAdapter `父类 `AbstractEndpointMvcAdapter`的实现

  ```java
  public abstract class AbstractEndpointMvcAdapter<E extends Endpoint<?>>
  	implements NamedMvcEndpoint {
  	...
  	
  	protected Object invoke() {
  		if (!this.delegate.isEnabled()) {
  			// Shouldn't happen - shouldn't be registered when delegate's disabled
  			return getDisabledResponse();
  		}
  		return this.delegate.invoke();
  	}
  	
  	...
  }
  ```

<hr>

####  RefreshEndpoint

  * 根据debug最终找到上文中的 `this.delegate` 为 `RefreshEndpoint` 源码如下

  ```java
  @ConfigurationProperties(prefix = "endpoints.refresh", ignoreUnknownFields = false)
  @ManagedResource
  public class RefreshEndpoint extends AbstractEndpoint<Collection<String>> {
  
  	private ContextRefresher contextRefresher;
  
  	public RefreshEndpoint(ContextRefresher contextRefresher) {
  		super("refresh");
  		this.contextRefresher = contextRefresher;
  	}
  
  	// 核心方法 利用 contextRefresher.refresh() 获取了此次刷新后改变的key
  	@ManagedOperation
  	public String[] refresh() {
  		Set<String> keys = contextRefresher.refresh();
  		return keys.toArray(new String[keys.size()]);
  	}
  
  	@Override
  	public Collection<String> invoke() {
  		return Arrays.asList(refresh());
  	}
  }
  ```



#### 尝试自动刷新

* 如何进行自动刷新思路如下

  * 从上面代码分析可知当我们访问客户端Refresh接口时本质上就是利用`contextRefresher.refresh()`进行刷新的
  * 我们可以利用定时器去处理，每过一段时间自动利用`contextRefresher.refresh()`刷新容器的上下文
  * 具体代码如下

```java
private final ContextRefresher contextRefresher;

// 注意使用的是 import org.springframework.core.env.Environment; 不是cloud包下的environment
private final Environment environment;

/**
 * 构造函数
 */
@Autowired
public ConfigClientApplication(ContextRefresher contextRefresher, Environment environment) {
    this.contextRefresher = contextRefresher;
    this.environment = environment;
}

@Scheduled(fixedRate = 5 * 1000, initialDelay = 3 * 1000)
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
    
```

* 实现效果如下，打印出来刷新上下文的日志

![](https://image.ibb.co/jj7p0K/1536307766372.jpg)



## 健康检查 

* 访问地址  观察项目启动日志 如下 应该访问的是 /health  或 /health.json GET 请求

```java
2018-09-07 16:24:47.482  INFO 26101 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/health || /health.json],methods=[GET],produces=[application/vnd.spring-boot.actuator.v1+json || application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.HealthMvcEndpoint.invoke(javax.servlet.http.HttpServletRequest,java.security.Principal)

```

* 访问http://localhost:8080/health（客户端地址）返回结果如下

```js
{
	status: "UP"
}
```

* 添加`HATEOAS`依赖 这里有一些网络上对`HATEOAS`的介绍  [HATEOAS 维基百科](https://en.wikipedia.org/wiki/HATEOAS)  [HATEOAS 简书](https://www.jianshu.com/p/65b9e54dee7d) 、[IBM介绍](https://www.ibm.com/developerworks/cn/java/j-lo-SpringHATEOAS/)

> REST客户端通过简单的固定[URL](https://en.wikipedia.org/wiki/Uniform_Resource_Locator)进入REST应用程序。客户端可能采取的所有未来操作都在服务器返回的[资源](https://en.wikipedia.org/wiki/Web_resource)表示中发现。用于这些表示的[媒体类型](https://en.wikipedia.org/wiki/Media_type)以及它们可能包含的[链接关系](https://en.wikipedia.org/wiki/Link_relation)是标准化的。客户端通过从表示中的链接中进行选择或通过以其媒体类型提供的其他方式操纵表示来转换应用程序状态。通过这种方式，RESTful交互由超媒体驱动，而不是带外信息。[[1\]  ](https://en.wikipedia.org/wiki/HATEOAS#cite_note-untangled2008-1) 						
>
> 																									[HATEOAS 维基百科](https://en.wikipedia.org/wiki/HATEOAS)																											

[Spring 官网介绍](https://docs.spring.io/spring-boot/docs/1.5.15.RELEASE/reference/htmlsingle/#boot-features-spring-hateoas) 版本为 Spring—Boot `1.5.15.RELEASE` 通过搜索`hateoas`可以获取到依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

* 本次为例 激活`actuator`生成的请求地址
```js
{"links":[{"rel":"self","href":"http://localhost:8080/actuator"},{"rel":"loggers","href":"http://localhost:8080/loggers"},{"rel":"configprops","href":"http://localhost:8080/configprops"},{"rel":"metrics","href":"http://localhost:8080/metrics"},{"rel":"dump","href":"http://localhost:8080/dump"},{"rel":"trace","href":"http://localhost:8080/trace"},{"rel":"mappings","href":"http://localhost:8080/mappings"},{"rel":"beans","href":"http://localhost:8080/beans"},{"rel":"env","href":"http://localhost:8080/env"},{"rel":"info","href":"http://localhost:8080/info"},{"rel":"auditevents","href":"http://localhost:8080/auditevents"},{"rel":"features","href":"http://localhost:8080/features"},{"rel":"autoconfig","href":"http://localhost:8080/autoconfig"},{"rel":"heapdump","href":"http://localhost:8080/heapdump"},{"rel":"health","href":"http://localhost:8080/health"}]}
```

* 端点URI :http://localhost:8080/health 实现类 `HealthEndpoint`

* 健康检查 `HealthIndicator`

### 自定义实现 HealthIndicator 

* 可以通过继承`AbstractHealthIndicator`覆盖实现 `doHealthCheck`方法

```java
public class MyHealthIndicator extends AbstractHealthIndicator {

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up().withDetail("MyHealthIndicator", "Go Go Go");
    }
}
  
```


* 生明为 bean

```java
@Bean
public MyHealthIndicator myHealth(){
    return new MyHealthIndicator();
}
```

####  关闭安全检查 （Debug 代码）

* 访问http://localhost:8080/health 结果如下

```json
{
   status: "UP"
}
```


* 正常情况下无法访问获取我们定义的信息，进行debug 进入`HealthMvcEndpoint#exposeHealthDetails`方法
```java
protected boolean exposeHealthDetails(HttpServletRequest request,Principal principal) {
    //因为没有权限 导致返回 false
    if (!this.secure) {
        return true;
    }
    List<String> roles = getRoles();
    for (String role : roles) {
        if (request.isUserInRole(role)) {
            return true;
        }
        if (isSpringSecurityAuthentication(principal)) {
            Authentication authentication = (Authentication) principal;
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String name = authority.getAuthority();
                if (role.equals(name)) {
                    return true;
                }
            }
        }
    }
    return false;
}
```

* 正常反应可以开始修改上文中的`this.secure`代码如下
```java
@ConfigurationProperties(prefix = "endpoints.health")
public class HealthMvcEndpoint extends AbstractEndpointMvcAdapter<HealthEndpoint>
		implements EnvironmentAware {

	private final boolean secure;
	...
}
```

* 第一反应进行修改 `application.properties`添加一段`endpoints.health.secure=false` 然而事实证明不行的
* 那只看看构造函数在哪里调用的了于是乎找了如下代码块`EndpointWebMvcManagementContextConfiguration`
```java
	@Bean
	@ConditionalOnBean(HealthEndpoint.class)
	@ConditionalOnMissingBean
	@ConditionalOnEnabledEndpoint("health")
	public HealthMvcEndpoint healthMvcEndpoint(HealthEndpoint delegate,
			ManagementServerProperties managementServerProperties) {
        
        //这端代码就是 HealthMvcEndpoint 的构造函数被调用的情况 调用的是
        
//       public HealthMvcEndpoint(HealthEndpoint delegate, boolean secure,
//			List<String> roles) {
//            super(delegate);
//            this.secure = secure;
//            setupDefaultStatusMapping();
//            this.roles = roles;
//	     }
        
		HealthMvcEndpoint healthMvcEndpoint = new HealthMvcEndpoint(delegate,
				this.managementServerProperties.getSecurity().isEnabled(),
				managementServerProperties.getSecurity().getRoles());
        // 所以 this.managementServerProperties.getSecurity().isEnabled() 控制是否可以访问
        // management.security.enabled=false 进行关闭
		if (this.healthMvcEndpointProperties.getMapping() != null) {
			healthMvcEndpoint
					.addStatusMapping(this.healthMvcEndpointProperties.getMapping());
		}
		return healthMvcEndpoint;
	}
```

```properties
management.security.enabled=false
```

* 最终更改后访问http://localhost:8080/health 结果如下

```json
{
    "status":"UP",
    "myHealth":{
        "status":"UP",
        "MyHealthIndicator":"Go Go Go"
    },
    "diskSpace":{
        "status":"UP",
        "total":120108089344,
        "free":38401208320,
        "threshold":10485760
    },
    "configServer":{
        "status":"UP",
        "propertySources":[
            "configClient",
            "file:/Users/chenmingming/workspace/java/my/config/start-dev.properties",
            "file:/Users/chenmingming/workspace/java/my/config/start.properties"
        ]
    }
}  
```

* DOWN 与 UP 应该是逻辑 与
```java
public class MyHealthIndicator extends AbstractHealthIndicator {
	//此处  builder.down()
    @Override 
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.down().withDetail("MyHealthIndicator", "Go Go Go");
    }
}
```

* 访问http://localhost:8080/health 结果如下 只有 myHealth 为DOWN 上面的 status 就为DOWN
```js
{
    "status":"DOWN",
    "myHealth":{
        "status":"DOWN",
        "MyHealthIndicator":"Go Go Go"
    },
    "diskSpace":{
        "status":"UP",
        "total":120108089344,
        "free":38402039808,
        "threshold":10485760
    },
    "configServer":{
        "status":"UP",
        "propertySources":[
            "configClient",
            "file:/Users/chenmingming/workspace/java/my/config/start-dev.properties",
            "file:/Users/chenmingming/workspace/java/my/config/start.properties"
        ]
    }
}
```

* 最后的作用 就是可以任意的输出你希望有的健康检查指标帮助定位问题