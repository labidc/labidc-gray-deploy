package com.labidc.gray.deploy.ribbon.rules;

import com.google.common.collect.Collections2;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;

import java.util.List;

/**
 * 会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务, 以及并发的连接数量
 * 超过阈值的服务,然后对剩余的服务列表按照轮询策略进行访问;
 *
 * @author ChenXingLiang
 * @date 2018-11-09 00:57
 **/
public class AvailabilityFilteringGrayDeployRule extends PredicateBasedGrayDeployRule {

    private AbstractServerPredicate predicate;

    public AvailabilityFilteringGrayDeployRule() {
        super();
        predicate = CompositePredicate.withPredicate(new AvailabilityPredicate(this, null))
                .addFallbackPredicate(AbstractServerPredicate.alwaysTrue())
                .build();
    }


    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        predicate = CompositePredicate.withPredicate(new AvailabilityPredicate(this, clientConfig))
                .addFallbackPredicate(AbstractServerPredicate.alwaysTrue())
                .build();
    }

    @Monitor(name = "AvailableServersCount", type = DataSourceType.GAUGE)
    public int getAvailableServersCount() {
        ILoadBalancer lb = getLoadBalancer();
        List<Server> servers = lb.getAllServers();
        if (servers == null) {
            return 0;
        }
        return Collections2.filter(servers, predicate.getServerOnlyPredicate()).size();
    }


    /**
     * This method is overridden to provide a more efficient implementation which does not iterate through
     * all servers. This is under the assumption that in most cases, there are more available instances
     * than not.
     */
    @Override
    public Server choose(Object key) {
        int count = 0;
        Server server = roundRobinRule.choose(key);
        while (count++ <= 10) {
            if (predicate.apply(new PredicateKey(server))) {
                return server;
            }
            server = roundRobinRule.choose(key);
        }
        return super.choose(key);
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return predicate;
    }

}
