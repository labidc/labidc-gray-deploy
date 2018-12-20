package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;

/**
 * @program: labidc-manager
 * @description: 灰度发布负载均衡规则工厂
 * @author: ChenXingLiang
 * @date: 2018-11-09 00:13
 **/
public class GrayDeployRibbonRuleFactory {

    /**
     * 规则名称
     * @param robinRuleName
     * @return
     */
    public static AbstractLoadBalancerRule CreateRoundRobinRule(String robinRuleName, AbstractDiscoveryProvider abstractDiscoveryProvider)
    {

        if(robinRuleName ==null) {
            return new RoundRobinGrayDeployRule();
        }

        switch (robinRuleName)
        {
            case "WeightedResponseTimeGrayDeployRule":
                return new WeightedResponseTimeGrayDeployRule();
            case "RandomGrayDeployRule":
                return new RandomGrayDeployRule();
            case "RoundRobinGrayDeployRule":
                return new RoundRobinGrayDeployRule();
            case "AvailabilityFilteringGrayDeployRule":
                return new AvailabilityFilteringGrayDeployRule();
            case "BestAvailableGrayDeployRule":
                return new BestAvailableGrayDeployRule();
            case "ZoneAvoidanceGrayDeployRule":
                return new ZoneAvoidanceGrayDeployRule();
            case "RetryGrayDeployRule":
                return new RetryGrayDeployRule();
            default:
                return new RoundRobinGrayDeployRule();
        }
    }
}
