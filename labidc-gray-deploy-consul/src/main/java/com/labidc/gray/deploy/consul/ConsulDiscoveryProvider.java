package com.labidc.gray.deploy.consul;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.labidc.gray.deploy.exception.DiscoveryServerException;
import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.discovery.ConsulServer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @program: labidc-manager
 * @description: consul发现中心服务提供者
 * @author: ChenXingLiang
 * @date: 2018-11-08 20:43
 **/
@Component(value = "DiscoveryProvider")
public class ConsulDiscoveryProvider extends AbstractDiscoveryProvider {

    @Value("${spring.cloud.consul.discovery.tags}")
    private List<String> tags = null;

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

    private static final int VERSION_SPLIT_LENGTH=2;
    @Override
    public String getCurrentVersion() {
        String versionStartsWith = GrayDeployConstant.VERSION + "=";

        Optional<String> optional = this.tags.stream().filter(c -> c.startsWith(versionStartsWith)).findFirst();
        if (optional.isPresent()) {
            String[] versionSplit = optional.get().split("=", VERSION_SPLIT_LENGTH);
            if (versionSplit.length == VERSION_SPLIT_LENGTH) {
                return versionSplit[versionSplit.length - 1];
            }
        }
        return null;
    }
}
