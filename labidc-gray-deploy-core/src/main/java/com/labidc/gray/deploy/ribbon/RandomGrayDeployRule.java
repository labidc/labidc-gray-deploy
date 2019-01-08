package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.labidc.gray.deploy.utils.SpringContextUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

/**
 * @program: labidc-manager
 * @description: 随机
 * @author: ChenXingLiang
 * @date: 2018-11-08 17:10
 **/
@Log
public class RandomGrayDeployRule extends AbstractLoadBalancerRule {


    @Resource(name = "DiscoveryProvider")
    @Autowired
    private AbstractDiscoveryProvider abstractDiscoveryProvider;

    Random rand;

    public RandomGrayDeployRule() {
        rand = new Random();
    }

    /**
     * Randomly choose from all living servers
     */
    @SuppressWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE")
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }

        if (this.abstractDiscoveryProvider == null) {
            this.abstractDiscoveryProvider = SpringContextUtils.getBean(AbstractDiscoveryProvider.class);
        }

        String requestHeaderVersion = abstractDiscoveryProvider.getRequestHeaderVersion();


        while (true) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = abstractDiscoveryProvider.getServicesAuto(lb.getReachableServers(), requestHeaderVersion);
            List<Server> allList = abstractDiscoveryProvider.getServicesAuto(lb.getAllServers(), requestHeaderVersion);


            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }

            int index = rand.nextInt(serverCount);
            Server server = upList.get(index);

            if (server != null && server.isAlive()) {
                return server;
            }

            /*
             * The only time this should happen is if the server list were
             * somehow trimmed. This is a transient condition. Retry after
             * yielding.
             */
            // Shouldn't actually happen.. but must be transient or a bug.
            Thread.yield();
        }
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
