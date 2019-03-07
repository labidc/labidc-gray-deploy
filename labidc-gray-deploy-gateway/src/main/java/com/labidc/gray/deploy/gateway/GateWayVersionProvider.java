package com.labidc.gray.deploy.gateway;

import com.labidc.gray.deploy.handler.VersionProvider;

import java.util.List;

/**
 * servicedemo
 * gateway版本提供者
 *
 * @author ChenXingLiang
 * @date 2019-01-03 13:29
 **/
public class GateWayVersionProvider implements VersionProvider {

    /**
     * 数据来源 {@link GateWayVersionHeaderReadFilter}
     */
    static final ThreadLocal<List<String>> GRAY_DEPLOY_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public List<String> getRequestHeaderVersions() {
        return GRAY_DEPLOY_THREAD_LOCAL.get();
    }


}
