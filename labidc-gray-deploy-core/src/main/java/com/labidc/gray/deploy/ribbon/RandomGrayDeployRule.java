package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

/**
 * @program: labidc-manager
 * @description: 随机
 * @author: ChenXingLiang
 * @date: 2018-11-08 17:10
 **/
public class RandomGrayDeployRule extends AbstractLoadBalancerRule {


    @Autowired
    private AbstractDiscoveryProvider abstractDiscoveryProvider;

    Random rand;

    public RandomGrayDeployRule() {
        rand = new Random();
    }

    /**
     * Randomly choose from all living servers
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE")
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;
        String requestHeaderVersion = abstractDiscoveryProvider.getRequestHeaderVersion();
        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }

            List<Server> upList = null;
            List<Server> allList = null;

            if(StringUtils.isEmpty(requestHeaderVersion)){
                upList = abstractDiscoveryProvider.getProdServices(lb.getReachableServers());
                allList = abstractDiscoveryProvider.getProdServices(lb.getAllServers());
            } else {
                upList = abstractDiscoveryProvider.getGrayServices(lb.getReachableServers(), requestHeaderVersion);
                allList =  abstractDiscoveryProvider.getGrayServices(lb.getAllServers(), requestHeaderVersion);
            }

            if(StringUtils.isNotEmpty(requestHeaderVersion) &&
                    (upList.size()==0 || allList.size()==0)) {
                upList = abstractDiscoveryProvider.getProdServices(lb.getReachableServers());
                allList = abstractDiscoveryProvider.getProdServices(lb.getAllServers());
            }


            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }

            int index = rand.nextInt(serverCount);
            server = upList.get(index);

            if (server == null) {
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
        }

        return server;

    }

    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(), key);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        // TODO Auto-generated method stub

    }
}
