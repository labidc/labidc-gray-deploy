package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.filter.ServerFilter;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;

import java.util.List;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class GrayDeployLoadBalancer<T extends Server> extends ZoneAwareLoadBalancer<T> {
    private ServerFilter serverFilter;

    public GrayDeployLoadBalancer(IClientConfig config, IRule rule, IPing ping, ServerList<T> serverList,
                                  ServerListFilter<T> serverListFilter, ServerListUpdater serverListUpdater,
                                  ServerFilter serverFilter) {
        super(config, rule, ping, serverList,
                serverListFilter, serverListUpdater);

        this.serverFilter = serverFilter;
    }


    @Override
    public List<Server> getReachableServers() {
        return serverFilter.getServicesAuto(super.getReachableServers());
    }

    @Override
    public List<Server> getAllServers() {
        return serverFilter.getServicesAuto(super.getAllServers());
    }
}
