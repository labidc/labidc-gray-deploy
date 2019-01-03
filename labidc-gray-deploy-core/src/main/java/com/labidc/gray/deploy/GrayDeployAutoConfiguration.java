package com.labidc.gray.deploy;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.labidc.gray.deploy.properties.GrayDeployProerties;
import com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleFactory;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
}
