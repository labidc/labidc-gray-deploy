package com.labidc.gray.deploy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: labidc-manager
 * @description: 自动配置文件
 * @author: ChenXingLiang
 * @date: 2018-11-09 00:04
 **/
@ConfigurationProperties("spring.gray.deploy")
@Component
@Data
public class GrayDeployProerties {

    /**
     * 负载均衡规则
     */
    private String ribbonRule;
}
