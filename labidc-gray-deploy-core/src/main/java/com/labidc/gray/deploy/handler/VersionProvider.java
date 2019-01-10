package com.labidc.gray.deploy.handler;


import org.apache.commons.lang.ObjectUtils;

import java.util.Map;

/**
 * @program: servicedemo
 * @description: 版本提供者
 * @author: ChenXingLiang
 * @date: 2019-01-03 13:19
 **/
public interface VersionProvider {

    /**
     * 获取版本号
     * @return
     */
    String getRequestHeaderVersion();

    /**
     * 获取自定义数据 默认实现了获取header所有数据 如果有需要 自行实现
     * @return
     */
    Map<String,Object> getRequestSelfData();

    /**
     * 根据key 获取自定义数据  内部有强转注意 实际类型
     */
    default <E> E getRequestSelfData(String key){
        Map<String, Object> requestSelfData = this.getRequestSelfData();
        if(requestSelfData == null){
            return null;
        }

        return (E) requestSelfData.get(key);
    }

    /**
     * 根据key 获取自定义数据  内部有强转注意 实际类型
     */
    default <E> E defaultRequestSelfData(String key,E defaultValue){
        E data = getRequestSelfData(key);

        return (E) ObjectUtils.defaultIfNull(data,defaultValue);
    }
}
