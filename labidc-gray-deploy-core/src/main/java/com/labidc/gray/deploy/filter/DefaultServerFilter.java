package com.labidc.gray.deploy.filter;

import com.labidc.gray.deploy.handler.DiscoveryProvider;
import com.labidc.gray.deploy.handler.VersionProvider;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class DefaultServerFilter extends AbstractServerFilter {
    @Autowired
    protected DiscoveryProvider discoveryProvider;

    @Autowired
    protected VersionProvider versionProvider;


    @Override
    public List<Server> getProdServices(List<Server> serverList) {
        return serverList.stream().filter((item) -> StringUtils.isEmpty(discoveryProvider.getVersion(item))).collect(toList());
    }

    @Override
    public List<Server> getGrayServices(List<Server> serverList) {

        if (StringUtils.isBlank(versionProvider.getRequestHeaderVersion())) {
            return Collections.emptyList();
        }

        String requestHeaderVersion = versionProvider.getRequestHeaderVersion().toUpperCase().trim();

        return serverList.stream().filter((item) -> {
            String version = discoveryProvider.getVersion(item);
            if (StringUtils.isEmpty(version)) {
                return false;
            }

            return requestHeaderVersion.equals(discoveryProvider.getVersion(item).toUpperCase().trim());
        }).collect(toList());
    }
}
