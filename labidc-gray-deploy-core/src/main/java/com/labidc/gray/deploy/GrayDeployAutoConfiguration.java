package com.labidc.gray.deploy;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.labidc.gray.deploy.properties.GrayDeployProerties;
import com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleFactory;
import com.netflix.loadbalancer.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @program: labidc-manager
 * @description: 自动配置对象
 * @author: ChenXingLiang
 * @date: 2018-11-08 20:46
 **/

@ComponentScan(value = "com.labidc.gray.deploy")
@Configuration
public class GrayDeployAutoConfiguration {


    @Resource(name="DiscoveryProvider")
    @Autowired
    private AbstractDiscoveryProvider abstractDiscoveryProvider;

    /**
     * 灰度发布配置规则
     */
    @Autowired
    private GrayDeployProerties grayDeployProerties;

    /**
     * 日志控制器
     */
    private static final Logger logger = LoggerFactory.getLogger(GrayDeployAutoConfiguration.class);


    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /**
     * 加载自定义负载均衡器
     * @return
     */
    @Bean
    public IRule getLoadBalancedRule() {
        logger.debug("======================加载了负载均衡器");
        if(this.grayDeployProerties == null){
            return GrayDeployRibbonRuleFactory.CreateRoundRobinRule(null, this.abstractDiscoveryProvider);
        }
        return GrayDeployRibbonRuleFactory.CreateRoundRobinRule(this.grayDeployProerties.getRibbonRule(), this.abstractDiscoveryProvider);
    }
}
