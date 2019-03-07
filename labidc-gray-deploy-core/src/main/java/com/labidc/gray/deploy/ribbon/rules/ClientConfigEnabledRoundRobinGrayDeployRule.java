package com.labidc.gray.deploy.ribbon.rules;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

/**
 * labidc-manager
 * 客户端配置开启轮询模式
 * 包含了轮询模式
 *
 * @author ChenXingLiang
 * @date 2018-11-13 18:50
 **/
public class ClientConfigEnabledRoundRobinGrayDeployRule extends AbstractLoadBalancerRule {

    RoundRobinGrayDeployRule roundRobinRule = new RoundRobinGrayDeployRule();

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        roundRobinRule = new RoundRobinGrayDeployRule();
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(lb);
        roundRobinRule.setLoadBalancer(lb);
    }

    @Override
    public Server choose(Object key) {
        if (roundRobinRule != null) {
            return roundRobinRule.choose(key);
        } else {
            throw new IllegalArgumentException(
                    "This class has not been initialized with the RoundRobinRule class");
        }
    }

}
