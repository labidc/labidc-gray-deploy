package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.labidc.gray.deploy.utils.SpringContextUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: labidc-manager
 * @description: 轮询
 * @author: ChenXingLiang
 * @date: 2018-11-09 00:55
 **/
public class RoundRobinGrayDeployRule extends AbstractLoadBalancerRule {


    @Autowired
    private AbstractDiscoveryProvider abstractDiscoveryProvider;

    private AtomicInteger nextServerCyclicCounter;
    private static final boolean AVAILABLE_ONLY_SERVERS = true;
    private static final boolean ALL_SERVERS = false;

    private static Logger log = LoggerFactory.getLogger(RoundRobinRule.class);


    public RoundRobinGrayDeployRule() {
        nextServerCyclicCounter = new AtomicInteger(0);
    }

    public RoundRobinGrayDeployRule(ILoadBalancer lb) {
        this();
        setLoadBalancer(lb);
    }


    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            log.warn("no load balancer");
            return null;
        }

        // RetryGrayDeployRule 依赖了 RoundRobinGrayDeployRule对象，内部new 创建，所以无法注入，这里需检查
        if(this.abstractDiscoveryProvider == null) {
            this.abstractDiscoveryProvider = SpringContextUtils.getBean(AbstractDiscoveryProvider.class);
        }
        String requestHeaderVersion = abstractDiscoveryProvider.getRequestHeaderVersion();
        int count = 0;
        while (count++ < 10) {

            List<Server> allServers = abstractDiscoveryProvider.getServices(lb.getAllServers(), requestHeaderVersion);
            int serverCount = allServers.size();

            if (serverCount == 0) {
                log.warn("No up servers available from load balancer: " + lb);
                return null;
            }

            int nextServerIndex = incrementAndGetModulo(serverCount);
            Server server = allServers.get(nextServerIndex);

            if (server != null) {
                return server;
            }

            /* Transient. */
            Thread.yield();
        }

        log.warn("No available alive servers after 10 tries from load balancer: "
                + lb);
        return null;
    }

    /**
     * Inspired by the implementation of {@link AtomicInteger#incrementAndGet()}.
     *
     * @param modulo The modulo to bound the value of the counter.
     * @return The next value.
     */
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(), key);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
    }

}
