package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.properties.GrayDeployProerties;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;

/**
 * @program: labidc-manager
 * @description: 灰度发布负载均衡规则工厂
 * @author: ChenXingLiang
 * @date: 2018-11-09 00:13
 **/
public class GrayDeployRibbonRuleFactory {


    /**
     * 获取当前设置的负载均衡规则
     */
    public static AbstractLoadBalancerRule createRoundRobinRule(GrayDeployProerties grayDeployProerties) {
        AbstractLoadBalancerRule rule = createRoundRobinRule(grayDeployProerties.getRibbonRule());

        if (grayDeployProerties.getRetry() != null && grayDeployProerties.getRetry()) {
            return new RetryGrayDeployRule(rule, grayDeployProerties.getMaxRetryMillis());
        }

        return rule;
    }

    /**
     * 获取当前设置的负载均衡规则
     */
    private static AbstractLoadBalancerRule createRoundRobinRule(GrayDeployRibbonRuleEnum ruleEnum) {
        if (ruleEnum == null) {
            return new RoundRobinGrayDeployRule();
        }

        switch (ruleEnum) {
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
            //case RETRY:
            //    return new RetryGrayDeployRule();
            default:
                return new RoundRobinGrayDeployRule();
        }
    }
}
