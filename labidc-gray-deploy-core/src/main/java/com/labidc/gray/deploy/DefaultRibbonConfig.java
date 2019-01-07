package com.labidc.gray.deploy;

import com.labidc.gray.deploy.properties.GrayDeployProerties;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.ServerListSubsetFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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



    @Bean
    public IRule ribbonRule(GrayDeployProerties grayDeployProerties) {
        return grayDeployProerties.loadRibbonRule();
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
