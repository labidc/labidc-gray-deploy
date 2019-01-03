package com.labidc.gray.deploy.gateway;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.ribbon.*;

/**
 * @program: servicedemo
 * @description: 自定义RibbonLoadBalancer
 * @author: ChenXingLiang
 * @date: 2019-01-02 15:45
 **/
public class GateWayRibbonLoadBalancerClient extends RibbonLoadBalancerClient {

    private SpringClientFactory clientFactory;
    private static final ThreadLocal<String> grayDeployThreadLocal = new ThreadLocal<String>();

    public GateWayRibbonLoadBalancerClient(SpringClientFactory clientFactory) {
        super(clientFactory);
        this.clientFactory = clientFactory;

    }

    public ServiceInstance choose(String serviceId, String version) {
        GateWayVersionProvider.grayDeployThreadLocal.set(version);
        Server server = this.getServer(serviceId);
        return server == null ? null : new RibbonLoadBalancerClient.RibbonServer(serviceId, server, this.isSecure(server, serviceId), this.serverIntrospector(serviceId).getMetadata(server));
    }

    /**
     * 复制原方法
     * @param serviceId
     * @return
     */
    @Override
    public ServiceInstance choose(String serviceId) {
       return super.choose(serviceId);
    }

    /**
     * 复制原方法
     * @param server
     * @param serviceId
     * @return
     */
    private boolean isSecure(Server server, String serviceId) {
        IClientConfig config = this.clientFactory.getClientConfig(serviceId);
        ServerIntrospector serverIntrospector = serverIntrospector(serviceId);
        return RibbonUtils.isSecure(config, serverIntrospector, server);
    }

    /**
     * 复制的原方法
     * @param serviceId
     * @return
     */
    private ServerIntrospector serverIntrospector(String serviceId) {
        ServerIntrospector serverIntrospector = this.clientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }
}
