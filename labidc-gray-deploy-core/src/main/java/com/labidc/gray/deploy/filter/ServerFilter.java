package com.labidc.gray.deploy.filter;

import com.netflix.loadbalancer.Server;

import java.util.List;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public interface ServerFilter {
    /**
     * 自动判断返回生产服务和灰度服务
     * @param serverList
     * @return
     */
    List<Server> getServicesAuto(List<Server> serverList);
    /**
     * 返回生产服务
     *
     * @param serverList
     * @return
     */
    List<Server> getProdServices(List<Server> serverList);

    /**
     * 返回回灰服务
     *
     * @param serverList
     * @return
     */
    List<Server> getGrayServices(List<Server> serverList);
}
