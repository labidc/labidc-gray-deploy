package com.labidc.gray.deploy;

import com.labidc.gray.deploy.filter.DefaultServerFilter;
import com.labidc.gray.deploy.filter.ServerFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @program: labidc-manager
 * @description: 自动配置对象
 * @author: ChenXingLiang
 * @date: 2018-11-08 20:46
 **/

@ComponentScan(value = "com.labidc.gray.deploy")
@Configuration
@RibbonClients(defaultConfiguration = DefaultRibbonConfig.class)
public class GrayDeployAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ServerFilter.class)
    public ServerFilter serverFilter(){
        return new DefaultServerFilter();
    }
}
