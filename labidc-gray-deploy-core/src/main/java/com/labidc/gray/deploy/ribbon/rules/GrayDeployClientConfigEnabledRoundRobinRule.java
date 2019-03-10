package com.labidc.gray.deploy.ribbon.rules;

import com.labidc.gray.deploy.ribbon.GrayDeployILoadBalancerWrapper;
import com.netflix.loadbalancer.ClientConfigEnabledRoundRobinRule;
import com.netflix.loadbalancer.ILoadBalancer;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class GrayDeployClientConfigEnabledRoundRobinRule extends ClientConfigEnabledRoundRobinRule {
    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(new GrayDeployILoadBalancerWrapper(lb));
    }
}
