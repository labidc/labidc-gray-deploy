package com.labidc.gray.deploy;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.labidc.gray.deploy.properties.GrayDeployProerties;
import com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @program: servicedemo
 * @description: TODO
 * @author: ChenXingLiang
 * @date: 2018-12-28 16:48
 **/

@Configuration
public class DefaultRibbonConfig {

    /**
     * 日志控制器
     */
    private static final Logger logger = LoggerFactory.getLogger(GrayDeployAutoConfiguration.class);



    @Resource(name="DiscoveryProvider")
    @Autowired
    private AbstractDiscoveryProvider abstractDiscoveryProvider;

    /**
     * 灰度发布配置规则
     */
    @Autowired
    private GrayDeployProerties grayDeployProerties;

    @Bean
    public IRule ribbonRule() {

        if(this.grayDeployProerties == null){
            return GrayDeployRibbonRuleFactory.CreateRoundRobinRule(null, this.abstractDiscoveryProvider);
        }
        return GrayDeployRibbonRuleFactory.CreateRoundRobinRule(this.grayDeployProerties.getRibbonRule(), this.abstractDiscoveryProvider);
    }

    @Bean
    public IPing ribbonPing() {
        return new PingUrl();
    }

    @Bean
    public ServerListSubsetFilter serverListFilter() {
        ServerListSubsetFilter filter = new ServerListSubsetFilter();
        return filter;
    }
}
