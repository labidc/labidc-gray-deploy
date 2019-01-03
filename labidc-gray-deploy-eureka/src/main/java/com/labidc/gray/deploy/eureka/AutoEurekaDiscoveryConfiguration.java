package com.labidc.gray.deploy.eureka;
import com.labidc.gray.deploy.GrayDeployAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @program: labidc-manager
 * @description: 自动配置启动
 * @author: ChenXingLiang
 * @date: 2018-11-10 13:29
 **/
@Import({GrayDeployAutoConfiguration.class})
@ComponentScan(value = "com.labidc.gray.deploy.eureka")
@AutoConfigureBefore(GrayDeployAutoConfiguration.class)
public class AutoEurekaDiscoveryConfiguration {
}
