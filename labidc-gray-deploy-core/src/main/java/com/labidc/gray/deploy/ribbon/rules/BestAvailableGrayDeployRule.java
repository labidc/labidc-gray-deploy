package com.labidc.gray.deploy.ribbon.rules;

import com.labidc.gray.deploy.filter.ServerFilter;
import com.labidc.gray.deploy.utils.SpringContextUtils;
import com.netflix.loadbalancer.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务, 然后选择一个并发量最小的服务;
 * 实际上是继承的轮询模式，再做的一个升级
 *
 * @author ChenXingLiang
 * @date 2018-11-09 01:15
 **/
public class BestAvailableGrayDeployRule extends ClientConfigEnabledRoundRobinGrayDeployRule {

    @Autowired
    private ServerFilter serverFilter;

    private LoadBalancerStats loadBalancerStats;

    @Override
    public Server choose(Object key) {
        if (loadBalancerStats == null) {
            return super.choose(key);
        }

        if (this.serverFilter == null) {
            this.serverFilter = SpringContextUtils.getBean(ServerFilter.class);
        }

        List<Server> serverList = serverFilter.getServicesAuto(getLoadBalancer().getAllServers());

        int minimalConcurrentConnections = Integer.MAX_VALUE;
        long currentTime = System.currentTimeMillis();
        Server chosen = null;
        for (Server server : serverList) {
            ServerStats serverStats = loadBalancerStats.getSingleServerStat(server);
            if (!serverStats.isCircuitBreakerTripped(currentTime)) {
                int concurrentConnections = serverStats.getActiveRequestsCount(currentTime);
                if (concurrentConnections < minimalConcurrentConnections) {
                    minimalConcurrentConnections = concurrentConnections;
                    chosen = server;
                }
            }
        }
        if (chosen == null) {
            return super.choose(key);
        } else {
            return chosen;
        }
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(lb);
        if (lb instanceof AbstractLoadBalancer) {
            loadBalancerStats = ((AbstractLoadBalancer) lb).getLoadBalancerStats();
        }
    }
}
