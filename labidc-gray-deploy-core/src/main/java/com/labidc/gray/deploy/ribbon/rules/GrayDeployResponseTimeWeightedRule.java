package com.labidc.gray.deploy.ribbon.rules;

import com.labidc.gray.deploy.ribbon.GrayDeployILoadBalancerWrapper;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.ResponseTimeWeightedRule;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class GrayDeployResponseTimeWeightedRule extends ResponseTimeWeightedRule {
    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(new GrayDeployILoadBalancerWrapper(lb));
    }
}
