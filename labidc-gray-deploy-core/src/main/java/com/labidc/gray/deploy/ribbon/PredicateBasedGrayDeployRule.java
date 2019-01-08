package com.labidc.gray.deploy.ribbon;

import com.google.common.base.Optional;
import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.labidc.gray.deploy.utils.SpringContextUtils;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: labidc-manager
 * @description: 断言基类，继承 ClientConfigEnabledRoundRobinGrayDeployRule 类
 * 默认是轮询模式，因为ClientConfigEnabledRoundRobinGrayDeployRule 包含了轮询
 * @author: ChenXingLiang
 * @date: 2018-11-13 19:21
 **/
public abstract class PredicateBasedGrayDeployRule extends ClientConfigEnabledRoundRobinGrayDeployRule {

    @Autowired
    private AbstractDiscoveryProvider abstractDiscoveryProvider;

    /**
     * Method that provides an instance of {@link AbstractServerPredicate} to be used by this class.
     *
     */
    public abstract AbstractServerPredicate getPredicate();

    /**
     * Get a server by calling {@link AbstractServerPredicate#chooseRandomlyAfterFiltering(java.util.List, Object)}.
     * The performance for this method is O(n) where n is number of servers to be filtered.
     */
    @Override
    public Server choose(Object key) {
        ILoadBalancer lb = getLoadBalancer();

        if(this.abstractDiscoveryProvider == null) {
            this.abstractDiscoveryProvider = SpringContextUtils.getBean(AbstractDiscoveryProvider.class);
        }
        String requestHeaderVersion = abstractDiscoveryProvider.getRequestHeaderVersion();

        List<Server> serverList =  abstractDiscoveryProvider.getServicesAuto(getLoadBalancer().getAllServers(), requestHeaderVersion);

        Optional<Server> server = getPredicate().chooseRoundRobinAfterFiltering(serverList, key);
        if (server.isPresent()) {
            return server.get();
        } else {
            return null;
        }
    }
}
