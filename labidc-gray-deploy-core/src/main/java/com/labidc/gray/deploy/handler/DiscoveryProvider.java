package com.labidc.gray.deploy.handler;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.netflix.loadbalancer.Server;

import java.util.Map;

/**
 * 注册发现服务提供者接口
 *
 * @author ChenXingLiang
 * @date 2018-11-08 17:44
 **/
public interface DiscoveryProvider {

    /**
     * 获取元数据
     */
    Map<String, String> getServerMetadata(Server server);


    /**
     * 获取当前服务的版本号
     *
     * @return 如果不存在版本号，则为null
     */
    String getCurrentVersion();

    /**
     * 根据当前服务对象的元数据字段获取到服务的版本号
     */
    default String getVersion(Server server) {
        return this.getServerMetadata(server).get(GrayDeployConstant.VERSION);
    }


}
