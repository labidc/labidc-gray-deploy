package com.labidc.gray.deploy;

import com.labidc.gray.deploy.properties.GrayDeployProerties;
import com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleFactory;
import com.netflix.loadbalancer.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenXingLiang
 * @date 2018-12-28 16:48
 **/

@Configuration
public class DefaultRibbonConfig {

    /**
     * 日志控制器
     */
    private static final Logger logger = LoggerFactory.getLogger(GrayDeployAutoConfiguration.class);


    @Bean
    public IRule ribbonRule(GrayDeployProerties grayDeployProerties) {
        return GrayDeployRibbonRuleFactory.createRoundRobinRule(grayDeployProerties);
    }

    //@Bean
    //public ServerListSubsetFilter serverListFilter() {
    //    ServerListSubsetFilter filter = new ServerListSubsetFilter();
    //    return filter;
    //}
}
