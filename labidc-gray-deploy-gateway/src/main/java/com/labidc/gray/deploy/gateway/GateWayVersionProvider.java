package com.labidc.gray.deploy.gateway;

import com.labidc.gray.deploy.handler.VersionProvider;

/**
 * @program: servicedemo
 * @description: gateway版本提供者
 * @author: ChenXingLiang
 * @date: 2019-01-03 13:29
 **/
public class GateWayVersionProvider implements VersionProvider {

    /**
     * 数据来源 {@link GateWayLoadBalancerClientFilter}
     */
    static final ThreadLocal<String> GRAY_DEPLOY_THREAD_LOCAL = new ThreadLocal<String>();

    @Override
    public String getRequestHeaderVersion() {
        return GRAY_DEPLOY_THREAD_LOCAL.get();
    }
}
