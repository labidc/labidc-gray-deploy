package com.labidc.gray.deploy.consul;

import com.labidc.gray.deploy.GrayDeployAutoConfiguration;
import com.labidc.gray.deploy.handler.DiscoveryProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * labidc-manager
 * 自动配置启动
 *
 * @author ChenXingLiang
 * @date 2018-11-10 13:26
 **/
@Import({GrayDeployAutoConfiguration.class})
@ComponentScan(value = "com.labidc.gray.deploy.consul")
@AutoConfigureBefore(GrayDeployAutoConfiguration.class)
public class AutoConsulGrayDeployConfiguration {
    @Bean
    @ConditionalOnMissingBean(DiscoveryProvider.class)
    public DiscoveryProvider discoveryProvider() {
        return new ConsulDiscoveryProvider();
    }
}
