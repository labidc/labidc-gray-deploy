package com.labidc.gray.deploy.gateway;

import com.labidc.gray.deploy.GrayDeployAutoConfiguration;
import com.labidc.gray.deploy.handler.DiscoveryProvider;
import com.labidc.gray.deploy.handler.VersionProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: servicedemo
 * @description: 自动加载
 * @author: ChenXingLiang
 * @date: 2019-01-02 13:42
 **/
@Configuration
@Import({GrayDeployAutoConfiguration.class})
@ComponentScan(value = "com.labidc.gray.deploy.gateway")
public class AutoGateWayConfiguration {


    @Bean
    @ConditionalOnMissingBean(VersionProvider.class)
    public GateWayVersionProvider versionProvider() {
        return new GateWayVersionProvider();
    }

    @Bean
    @ConditionalOnMissingBean(DiscoveryProvider.class)
    public DiscoveryProvider discoveryProvider() {
        return new ConsulDiscoveryProvider();
    }

    @Bean
    @ConditionalOnBean(GateWayVersionProvider.class)
    public GlobalFilter gateWayVersionHeaderReadFilter() {
        return new GateWayVersionHeaderReadFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.gray.deploy", name = "readConfigVersion", matchIfMissing = false)
    public GlobalFilter gateWayVersionHeaderReadConfigFilter() {
        return new GateWayVersionHeaderReadConfigFilter();
    }


}
