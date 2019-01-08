package com.labidc.gray.deploy.ribbon;

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
    public static AbstractLoadBalancerRule createRoundRobinRule(GrayDeployRibbonRuleEnum robinRuleName)
    {

        if(robinRuleName ==null) {
            return new RoundRobinGrayDeployRule();
        }

        switch (robinRuleName)
        {
            case WEIGHTED_RESPONSE_TIME:
                return new WeightedResponseTimeGrayDeployRule();
            case RANDOM:
                return new RandomGrayDeployRule();
            case ROUND_ROBIN:
                return new RoundRobinGrayDeployRule();
            case AVAILABILITY_FILTERING:
                return new AvailabilityFilteringGrayDeployRule();
            case BEST_AVAILABLE:
                return new BestAvailableGrayDeployRule();
            case ZONE_AVOIDANCE:
                return new ZoneAvoidanceGrayDeployRule();
            case RETRY:
                return new RetryGrayDeployRule();
            default:
                return new RoundRobinGrayDeployRule();
        }
    }
}
