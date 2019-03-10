package com.labidc.gray.deploy.ribbon.rules;

import com.labidc.gray.deploy.ribbon.GrayDeployILoadBalancerWrapper;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.WeightedResponseTimeRule;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class GrayDeployWeightedResponseTimeRule extends WeightedResponseTimeRule {
    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(new GrayDeployILoadBalancerWrapper(lb));
    }

}
