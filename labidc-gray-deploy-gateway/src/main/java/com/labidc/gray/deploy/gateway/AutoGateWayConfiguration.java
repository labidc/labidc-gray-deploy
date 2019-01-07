package com.labidc.gray.deploy.gateway;

import com.labidc.gray.deploy.GrayDeployAutoConfiguration;
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
@ComponentScan(value = "com.labidc.gray.deploy")
public class AutoGateWayConfiguration {



    @Bean
    public GlobalFilter loadLoadBalancerFilter(){
        return new GateWayLoadBalancerClientFilter();
    }


}
