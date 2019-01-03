package com.labidc.gray.deploy.gateway;


import com.labidc.gray.deploy.constant.GrayDeployConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

/**
 * @program: servicedemo
 * @description: 自定义过滤器
 * @author: ChenXingLiang
 * @date: 2019-01-02 14:24
 **/
public class GateWayLoadBalancerClientFilter implements GlobalFilter, Ordered {

    /**
     * 负载均衡客户端对象
     */
    private final LoadBalancerClient loadBalancer;


    /**
     * 日志打印
     */
    private static final Log log = LogFactory.getLog(GateWayLoadBalancerClientFilter.class);

    /*
     * 过滤器加载顺序
     */
    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10099;

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }



    /**
     * 自定义客户端
     * @param loadBalancer
     */
    public GateWayLoadBalancerClientFilter(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI url = (URI)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = (String)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || !"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix)) {
            return chain.filter(exchange);
        } else {
            ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);
            log.trace("LoadBalancerClientFilter url before: " + url);
            ServiceInstance instance = null;
            String requestVersion = null;
            if(exchange.getRequest().getHeaders().get(GrayDeployConstant.VERSION) != null) {
                requestVersion = exchange.getRequest().getHeaders().get(GrayDeployConstant.VERSION).get(0);
            }
            instance = ((GateWayRibbonLoadBalancerClient)this.loadBalancer).choose(url.getHost(), requestVersion);

            if (instance == null) {
                throw new NotFoundException("Unable to find instance for " + url.getHost());
            } else {
                URI uri = exchange.getRequest().getURI();
                String overrideScheme = null;
                if (schemePrefix != null) {
                    overrideScheme = url.getScheme();
                }

                URI requestUrl = this.loadBalancer.reconstructURI(new GateWayLoadBalancerClientFilter.DelegatingServiceInstance(instance, overrideScheme), uri);
                log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
                exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);

                return chain.filter(exchange);
            }
        }
    }

    class DelegatingServiceInstance implements ServiceInstance {
        final ServiceInstance delegate;
        private String overrideScheme;

        DelegatingServiceInstance(ServiceInstance delegate, String overrideScheme) {
            this.delegate = delegate;
            this.overrideScheme = overrideScheme;
        }

        @Override
        public String getServiceId() {
            return this.delegate.getServiceId();
        }
        @Override
        public String getHost() {
            return this.delegate.getHost();
        }
        @Override
        public int getPort() {
            return this.delegate.getPort();
        }
        @Override
        public boolean isSecure() {
            return this.delegate.isSecure();
        }
        @Override
        public URI getUri() {
            return this.delegate.getUri();
        }
        @Override
        public Map<String, String> getMetadata() {
            return this.delegate.getMetadata();
        }
        @Override
        public String getScheme() {
            String scheme = this.delegate.getScheme();
            return scheme != null ? scheme : this.overrideScheme;
        }
    }

}
