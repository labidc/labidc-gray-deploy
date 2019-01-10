package com.labidc.gray.deploy.gateway;


import com.labidc.gray.deploy.constant.GrayDeployConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: servicedemo
 * @description: 自定义过滤器
 * @author: ChenXingLiang
 * @date: 2019-01-02 14:24
 **/
public class GateWayLoadBalancerClientFilter implements GlobalFilter, Ordered {

    /**
     * 日志打印
     */
    private static final Log log = LogFactory.getLog(GateWayLoadBalancerClientFilter.class);

    /**
     * 过滤器加载顺序
     */
    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10099;


    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String versionHeader = headers.getFirst(GrayDeployConstant.VERSION);
        if (!StringUtils.isEmpty(versionHeader)) {
            GateWayVersionProvider.GRAY_DEPLOY_THREAD_LOCAL.set(versionHeader);
        } else {
            GateWayVersionProvider.GRAY_DEPLOY_THREAD_LOCAL.remove();
        }

        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, List<String>> headerEntry : headers.entrySet()) {
            List<String> value = headerEntry.getValue();
            map.put(headerEntry.getKey(), CollectionUtils.isEmpty(value) ? null : value.get(0));
        }
        GateWayVersionProvider.GRAY_DEPLOY_SELF_DATA_THREAD_LOCAL.set(map);


        return chain.filter(exchange);
    }
}
