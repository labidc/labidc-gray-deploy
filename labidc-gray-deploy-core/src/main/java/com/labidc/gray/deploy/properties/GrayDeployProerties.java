package com.labidc.gray.deploy.properties;

import com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleEnum;
import com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleFactory;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
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
    private GrayDeployRibbonRuleEnum ribbonRuleName = GrayDeployRibbonRuleEnum.ROUND_ROBIN;

    /**
     * 获取当前设置的负载均衡规则
     * @return
     */
    public AbstractLoadBalancerRule loadRibbonRule(){
        return GrayDeployRibbonRuleFactory.CreateRoundRobinRule(this.ribbonRuleName);
    }
}
