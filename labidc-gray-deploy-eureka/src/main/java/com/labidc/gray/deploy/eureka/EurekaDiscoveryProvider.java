package com.labidc.gray.deploy.eureka;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.labidc.gray.deploy.exception.DiscoveryServerException;
import com.labidc.gray.deploy.handler.DiscoveryProvider;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @program: labidc-manager
 * @description: eureka发现中心服务提供者
 * @author: ChenXingLiang
 * @date: 2018-11-08 20:43
 **/
public class EurekaDiscoveryProvider implements DiscoveryProvider {


    @Autowired
    private EurekaInstanceConfigBean eurekaInstanceConfigBean;

    @Override
    public Map<String, String> getServerMetadata(Server server) {
        if (server instanceof DiscoveryEnabledServer) {
            DiscoveryEnabledServer eurekaServer = (DiscoveryEnabledServer) server;
            return eurekaServer.getInstanceInfo().getMetadata();
        }
        throw new DiscoveryServerException("该服务器实例不是Eureka提供，它是："+server.getClass().getSimpleName());
    }

    @Override
    public String getCurrentVersion() {
        Map<String, String> metadataMap = this.eurekaInstanceConfigBean.getMetadataMap();
        if (metadataMap != null && metadataMap.size() > 0) {
            String version = metadataMap.get(GrayDeployConstant.VERSION);
            if(StringUtils.isEmpty(version)){
                return null;
            }
            return version;
        }
        return null;
    }
}
