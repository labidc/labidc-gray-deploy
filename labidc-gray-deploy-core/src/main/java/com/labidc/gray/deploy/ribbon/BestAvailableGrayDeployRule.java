package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.netflix.loadbalancer.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: labidc-manager
 * @description: 会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务, 然后选择一个并发量最小的服务;
 * 实际上是继承的轮询模式，再做的一个升级
 * @author: ChenXingLiang
 * @date: 2018-11-09 01:15
 **/
public class BestAvailableGrayDeployRule extends ClientConfigEnabledRoundRobinGrayDeployRule {

    @Autowired
    private AbstractDiscoveryProvider abstractDiscoveryProvider;

    private LoadBalancerStats loadBalancerStats;

    @Override
    public Server choose(Object key) {
        if (loadBalancerStats == null) {
            return super.choose(key);
        }
        
        String requestHeaderVersion = this.abstractDiscoveryProvider.getRequestHeaderVersion();
        List<Server> serverList = abstractDiscoveryProvider.getServices(getLoadBalancer().getAllServers(), requestHeaderVersion);

        int minimalConcurrentConnections = Integer.MAX_VALUE;
        long currentTime = System.currentTimeMillis();
        Server chosen = null;
        for (Server server: serverList) {
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
