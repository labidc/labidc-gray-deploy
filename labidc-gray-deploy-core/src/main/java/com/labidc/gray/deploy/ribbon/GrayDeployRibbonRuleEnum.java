package com.labidc.gray.deploy.ribbon;

import com.netflix.loadbalancer.IRule;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: servicedemo
 * @description: TODO
 * @author: ChenXingLiang
 * @date: 2019-01-07 10:50
 **/
@AllArgsConstructor
@Getter
public enum GrayDeployRibbonRuleEnum {
    //
    WEIGHTED_RESPONSE_TIME("WeightedResponseTimeGrayDeployRule"),
    RANDOM("RandomGrayDeployRule"),
    ROUND_ROBIN("RoundRobinGrayDeployRule"),
    AVAILABILITY_FILTERING("AvailabilityFilteringGrayDeployRule"),
    BEST_AVAILABLE("BestAvailableGrayDeployRule"),
    ZONE_AVOIDANCE("ZoneAvoidanceGrayDeployRule"),
    RETRY("RetryGrayDeployRule"),
    ;
    private String ruleName;
}
