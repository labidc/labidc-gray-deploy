package com.labidc.gray.deploy;

import com.labidc.gray.deploy.filter.ServerFilter;
import com.labidc.gray.deploy.ribbon.GrayDeployLoadBalancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientName;
import org.springframework.context.annotation.Bean;

/**
 * @author ChenXingLiang
 * @date 2018-12-28 16:48
 **/
@EnableConfigurationProperties
//@AutoConfigureBefore(RibbonClientConfiguration.class)
public class GrayDeployRibbonConfig {

    /**
     * 日志控制器
     */
    private static final Logger logger = LoggerFactory.getLogger(GrayDeployAutoConfiguration.class);


    @RibbonClientName
    private String name = "client";

    @Autowired
    private PropertiesFactory propertiesFactory;


    //@Autowired(required = false)
    //private List<RibbonClientSpecification> configurations = new ArrayList<>();


    //@Bean
    //public SpringClientFactory springClientFactory() {
    //    SpringClientFactory factory = new SpringClientFactory();
    //    factory.setConfigurations(this.configurations);
    //    return factory;
    //}
//
    //@Bean
    //@ConditionalOnMissingBean(LoadBalancerClient.class)
    //public LoadBalancerClient loadBalancerClient() {
    //    return new RibbonLoadBalancerClient(springClientFactory());
    //}

    @Bean
    @ConditionalOnMissingBean
    public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
                                            ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
                                            IRule rule, IPing ping, ServerListUpdater serverListUpdater,
                                            ServerFilter serverFilter) {
        if (this.propertiesFactory.isSet(ILoadBalancer.class, name)) {
            return this.propertiesFactory.get(ILoadBalancer.class, config, name);
        }
        return new GrayDeployLoadBalancer<>(config, rule, ping, serverList,
                serverListFilter, serverListUpdater, serverFilter);
    }
    //@Bean
    //@ConditionalOnMissingBean
    //public IRule ribbonRule(IClientConfig config) {
    //    if (this.propertiesFactory.isSet(IRule.class, name)) {
    //        IRule rule = this.propertiesFactory.get(IRule.class, config, name);
    //        return  new GrayDeployIRuleWrapper(rule);
    //    }
//
    //    ZoneAvoidanceRule rule = new ZoneAvoidanceRule();
    //    rule.initWithNiwsConfig(config);
    //    return new GrayDeployIRuleWrapper(rule);
    //}

    //@Bean
    //public IRule ribbonRule(GrayDeployProerties grayDeployProerties) {
    //    return GrayDeployRibbonRuleFactory.createRoundRobinRule(grayDeployProerties);
    //}

    //@Bean
    //public ServerListSubsetFilter serverListFilter() {
    //    ServerListSubsetFilter filter = new ServerListSubsetFilter();
    //    return filter;
    //}
}
