package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.filter.ServerFilter;
import com.labidc.gray.deploy.utils.SpringContextUtils;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class GrayDeployILoadBalancerWrapper implements ILoadBalancer {
    private ILoadBalancer loadBalancer;
    private ServerFilter serverFilterStore;

    public GrayDeployILoadBalancerWrapper(ILoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public ServerFilter getServerFilter() {
        if (serverFilterStore == null) {
            serverFilterStore = SpringContextUtils.getBean(ServerFilter.class);
        }

        return serverFilterStore;
    }

    @Override
    public void addServers(List<Server> newServers) {
        loadBalancer.addServers(newServers);
    }

    @Override
    public Server chooseServer(Object key) {
        return loadBalancer.chooseServer(key);
    }

    @Override
    public void markServerDown(Server server) {
        loadBalancer.markServerDown(server);
    }

    @Override
    public List<Server> getServerList(boolean availableOnly) {
        return loadBalancer.getServerList(availableOnly);
    }

    @Override
    public List<Server> getReachableServers() {
        return getServerFilter().getServicesAuto(loadBalancer.getReachableServers());
    }

    @Override
    public List<Server> getAllServers() {
        return getServerFilter().getServicesAuto(loadBalancer.getAllServers());
    }
}
