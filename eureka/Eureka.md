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

##  部分源码

* `EurekaClientConfigBean`是描述配置Eureka的服务端信息

```java
// EurekaClientConfigBean 部分源码
private Map<String, String> serviceUrl = new HashMap<>();
{
	this.serviceUrl.put(DEFAULT_ZONE, DEFAULT_URL);
}

public static final String DEFAULT_URL = "http://localhost:8761" + DEFAULT_PREFIX
			+ "/";
public static final String DEFAULT_ZONE = "defaultZone";
...

@Override
public List<String> getEurekaServerServiceUrls(String myZone) {
   String serviceUrls = this.serviceUrl.get(myZone);
   //先去寻找 map 中是否有 myZone 的值
   if (serviceUrls == null || serviceUrls.isEmpty()) {
       // 没有就走默认的常量
       serviceUrls = this.serviceUrl.get(DEFAULT_ZONE);
   }
   if (!StringUtils.isEmpty(serviceUrls)) {
      // 以逗号分割
      final String[] serviceUrlsSplit = StringUtils.commaDelimitedListToStringArray(serviceUrls);
      List<String> eurekaServiceUrls = new ArrayList<>(serviceUrlsSplit.length);
      for (String eurekaServiceUrl : serviceUrlsSplit) {
         if (!endsWithSlash(eurekaServiceUrl)) {
            eurekaServiceUrl += "/";
         }
         eurekaServiceUrls.add(eurekaServiceUrl);
      }
      return eurekaServiceUrls;
   }

   return new ArrayList<>();
}
```

### Eureka 客户端发现服务的过程

Eureka 客户端 ： `EurekaClient`  -> 关联应用集合 `Applications`

```java
public interface EurekaClient extends LookupService {
	public Applications getApplicationsForARegion(@Nullable String region);
    ...
}
```

`Applications`应用集合拥 -> 有多个应用的信息 `Application`

```java
// Applications
public void addApplication(Application app) {
    appNameApplicationMap.put(app.getName().toUpperCase(Locale.ROOT), app);
    addInstancesToVIPMaps(app);
    applications.add(app);
}
```

`Application`应用信息 -> 关联多个应用实例 `InstanceInfo`

```java
@XStreamImplicit
private final Set<InstanceInfo> instances;

private AtomicReference<List<InstanceInfo>> shuffledInstances = new AtomicReference<List<InstanceInfo>>();

private Map<String, InstanceInfo> instancesMap;

public void addInstance(InstanceInfo i) {
    instancesMap.put(i.getId(), i);
    synchronized (instances) {
        instances.remove(i);
        instances.add(i);
        isDirty = true;
    }
}
```

当Eureka客户端需要调用某个具体服务时，实际上是对应对象 `Application`关联的多个实例 `InstanceInfo` 

### 调整注册信息更新的时间间隔 

```yaml   

eureka:
  client:
  	# Eureka 客户端上传客户端信息的时间间隔默认30s 
  	instance-info-replication-interval-seconds: 5
  	# Eureka 客户端向服务端获取服务信息的时间间隔30s
  	registry-fetch-interval-seconds: 5
```

```java
// EurekaClientConfigBean

/**
 * Indicates how often(in seconds) to fetch the registry information from the eureka
 * server.
 */
private int registryFetchIntervalSeconds = 30;

/**
 * Indicates how often(in seconds) to replicate instance changes to be replicated to
 * the eureka server.
 */
private int instanceInfoReplicationIntervalSeconds = 30;
```
> Eureka 的应用信息同步的方式：拉模式   
Eureka 客户端上传信息：推模式

### 客户端实例id

日志信息

```java
DiscoveryClient_USER-SERVICE-PROVIDER/172.16.101.75:user-service-provider:7070 - registration status: 204
DiscoveryClient_USER-SERVICE-CONSUMER/172.16.101.75:user-service-consumer:8080 - registration status: 204
```
命名模式  `${hostname}:${spring.application.name}:${server.port}`

```yaml
### 修改客户端实例的id 一般不建议修改
eureka:
  instance:
    instance-id: ${spring.application.name}:${server.port}
```

### http 交互实现方式 

`ClientHttpRequestFactory`主要是实现接口 实现方式各有不同

![](https://image.ibb.co/cLZqEz/1537174812006.jpg)

```java
// 通过构造器方式修改
public RestTemplate restTemplate(){
	return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
}
```









