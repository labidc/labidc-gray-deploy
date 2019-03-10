package com.labidc.gray.deploy.ribbon.rules;

import com.labidc.gray.deploy.ribbon.GrayDeployILoadBalancerWrapper;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RetryRule;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class GrayDeployRetryRule extends RetryRule {
    public GrayDeployRetryRule() {
    }

    public GrayDeployRetryRule(IRule subRule) {
        super(subRule);
    }

    public GrayDeployRetryRule(IRule subRule, long maxRetryMillis) {
        super(subRule, maxRetryMillis);
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(new GrayDeployILoadBalancerWrapper(lb));
    }
}
