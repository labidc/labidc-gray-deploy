package com.labidc.gray.deploy.gateway;

import com.labidc.gray.deploy.handler.VersionProvider;

import java.util.Map;

/**
 * @program: servicedemo
 * @description: gateway版本提供者
 * @author: ChenXingLiang
 * @date: 2019-01-03 13:29
 **/
public class GateWayVersionProvider implements VersionProvider {

    static final ThreadLocal<String> GRAY_DEPLOY_THREAD_LOCAL = new ThreadLocal<String>();
    static final ThreadLocal<Map<String,Object>> GRAY_DEPLOY_SELF_DATA_THREAD_LOCAL = new ThreadLocal<Map<String,Object>>();

    @Override
    public String getRequestHeaderVersion() {
        return GRAY_DEPLOY_THREAD_LOCAL.get();
    }

    @Override
    public Map<String, Object> getRequestSelfData() {
        return GRAY_DEPLOY_SELF_DATA_THREAD_LOCAL.get();
    }
}
