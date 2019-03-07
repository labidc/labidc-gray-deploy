package com.labidc.gray.deploy.gateway;


import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.labidc.gray.deploy.handler.DiscoveryProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * servicedemo
 * 自定义过滤器
 *
 * @author ChenXingLiang
 * @date 2019-01-02 14:24
 **/
public class GateWayVersionHeaderReadConfigFilter implements GlobalFilter, Ordered {
    /**
     * 过滤器加载顺序 需要在 GateWayVersionHeaderReadFilter 执行之前加载
     * 服务选取 为 {@link GateWayVersionHeaderReadFilter} 加载顺序为 {@link GateWayVersionHeaderReadFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER}
     */
    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = GateWayVersionHeaderReadFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER - 10;
    /**
     * 日志打印
     */
    private static final Log log = LogFactory.getLog(GateWayVersionHeaderReadConfigFilter.class);
    @Autowired
    private DiscoveryProvider discoveryProvider;

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String versionHeader = headers.getFirst(GrayDeployConstant.VERSION);
        if (StringUtils.isEmpty(versionHeader)) {
            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
            builder.headers((o) -> o.set(GrayDeployConstant.VERSION, discoveryProvider.getCurrentVersion()));
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }

        return chain.filter(exchange);
    }
}
