package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

import static com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleEnum.RANDOM;
import static com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleEnum.WEIGHTED_RESPONSE_TIME;

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
    public static AbstractLoadBalancerRule CreateRoundRobinRule(GrayDeployRibbonRuleEnum robinRuleName)
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
