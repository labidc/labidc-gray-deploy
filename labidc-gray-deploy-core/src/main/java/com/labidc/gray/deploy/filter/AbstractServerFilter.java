package com.labidc.gray.deploy.filter;

import com.netflix.loadbalancer.Server;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public abstract class AbstractServerFilter implements ServerFilter {

    @Override
    public List<Server> getServicesAuto(List<Server> serverList) {
        List<Server> grayServices = this.getGrayServices(serverList);
        if (CollectionUtils.isNotEmpty(grayServices)
                && grayServices.stream().anyMatch(Server::isAlive)) {
            return grayServices;
        }

        return this.getProdServices(serverList);
    }
}
