package com.labidc.gray.deploy.gateway;


import com.labidc.gray.deploy.constant.GrayDeployConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

        String versionHeader = exchange.getRequest().getHeaders().getFirst(GrayDeployConstant.VERSION);
        if (!StringUtils.isEmpty(versionHeader)) {
            GateWayVersionProvider.grayDeployThreadLocal.set(versionHeader);
        }
        return chain.filter(exchange);
    }
}
