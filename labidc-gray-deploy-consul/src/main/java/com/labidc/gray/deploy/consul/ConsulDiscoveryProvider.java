package com.labidc.gray.deploy.consul;

import com.labidc.gray.deploy.exception.DiscoveryServerException;
import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.consul.discovery.ConsulServer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: labidc-manager
 * @description: consul发现中心服务提供者
 * @author: ChenXingLiang
 * @date: 2018-11-08 20:43
 **/
@Component(value = "DiscoveryProvider")
public class ConsulDiscoveryProvider extends AbstractDiscoveryProvider {
    /**
     * 日志控制器
     */
    private static final Logger logger = LoggerFactory.getLogger(ConsulDiscoveryProvider.class);

    @Override
    public Map<String, String> getServerMetadata(Server server) {

        logger.debug("获取服务的元数据");
        if (server instanceof ConsulServer) {
            ConsulServer consulServer = (ConsulServer) server;
            return consulServer.getMetadata();
        }
        throw new DiscoveryServerException("======================该服务器实例不是Consul提供它是："+server.getClass().getSimpleName());
    }
}
