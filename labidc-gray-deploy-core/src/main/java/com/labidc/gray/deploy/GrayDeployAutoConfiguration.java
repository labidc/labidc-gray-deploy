package com.labidc.gray.deploy;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
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

}
