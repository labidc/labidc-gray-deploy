灰度发布插件
======
消费者service安装：
-------
基于consul:
```
    <dependency>
        <groupId>labidc.manager</groupId>
        <artifactId>labidc-gray-deploy-consul</artifactId>
        <version>1.0.8</version>
  </dependency>
```

基于eureka:
```
    <dependency>
        <groupId>labidc.manager</groupId>
        <artifactId>labidc-gray-deploy-eureka</artifactId>
        <version>1.0.1</version>
  </dependency>
```

## 配置负载均衡算法，不设置默认以轮询方式负载均衡
```
spring:
  gray:
    deploy:
      ribbonRule: RoundRobinGrayDeployRule
```

## 支持的负载均衡算法与ribbon默认支持相同
```
1. BestAvailableGrayDeployRule
2. RandomGrayDeployRule
3. AvailabilityFilteringGrayDeployRule
4. RetryRuleGrayDeployRule
5. RoundRobinGrayDeployRule
6. WeightedResponseTimeGrayDeployRule
7. ZoneAvoidanceGrayDeployRule
```

生产者service安装：
-------
基于consul:
```
spring:
  cloud:
    consul:
      discovery:
        tags:
          version=1.0.1
```
基于eureka:
```
eureka:
  instance:
    metadataMap:
      version: 1.0.1
```