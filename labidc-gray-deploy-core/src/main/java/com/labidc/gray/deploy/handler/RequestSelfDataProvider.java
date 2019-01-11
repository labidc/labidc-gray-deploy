package com.labidc.gray.deploy.handler;

import org.apache.commons.lang.ObjectUtils;

import java.util.Map;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public interface RequestSelfDataProvider {
    /**
     * 获取自定义数据 默认实现了获取header所有数据 如果有其他需要 自行实现
     * gateway 需要自行实现 实现方式参考 {@link VersionProvider} 的 GateWayVersionProvider 实现
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
