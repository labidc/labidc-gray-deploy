package com.labidc.gray.deploy.filter;

import com.labidc.gray.deploy.handler.DiscoveryProvider;
import com.labidc.gray.deploy.handler.VersionProvider;
import com.labidc.gray.deploy.properties.GrayDeployProerties;
import com.netflix.loadbalancer.Server;
import org.apache.commons.collections.CollectionUtils;
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

    @Autowired
    private GrayDeployProerties grayDeployProerties;

   /* @Autowired(required = false)
    protected RequestSelfDataProvider requestSelfDataProvider;*/


    @Override
    public List<Server> getProdServices(List<Server> serverList) {

        List<String> defaultServiceVersions = grayDeployProerties.getDefaultServiceVersions();
        boolean notFindDefaultServiceVersions = CollectionUtils.isEmpty(defaultServiceVersions);

        List<Server> prodServices = serverList.stream().filter((item) -> {
            String version = discoveryProvider.getVersion(item);
            if (StringUtils.isEmpty(version)) {
                return true;
            }

            if (notFindDefaultServiceVersions) {
                return false;
            }

            return defaultServiceVersions.contains(version.toUpperCase().trim());
        }).collect(toList());

        if (CollectionUtils.isNotEmpty(prodServices)) {
            return prodServices;
        }

        if (Boolean.TRUE.equals(grayDeployProerties.getNoDefaultServiceReturnAll())) {
            return serverList;
        }

        return Collections.emptyList();
    }

    @Override
    public List<Server> getGrayServices(List<Server> serverList) {
        List<String> requestHeaderVersions = versionProvider.getRequestHeaderVersions();
        if (CollectionUtils.isEmpty(requestHeaderVersions)) {
            return Collections.emptyList();
        }

        return serverList.stream().filter((item) -> {
            String version = discoveryProvider.getVersion(item);
            if (StringUtils.isEmpty(version)) {
                return false;
            }

            return requestHeaderVersions.contains(version.toUpperCase().trim());
        }).collect(toList());
    }
}
