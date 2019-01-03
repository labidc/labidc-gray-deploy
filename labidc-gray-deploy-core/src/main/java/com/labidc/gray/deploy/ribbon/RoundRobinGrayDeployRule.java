package com.labidc.gray.deploy.ribbon;

import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import com.labidc.gray.deploy.utils.SpringContextUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
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
        Server server = null;
        int count = 0;
        while (server == null && count++ < 10) {

            List<Server> reachableServers = null;
            List<Server> allServers = null;
            if(StringUtils.isEmpty(requestHeaderVersion)){
                reachableServers = abstractDiscoveryProvider.getProdServices(lb.getReachableServers());
                allServers = abstractDiscoveryProvider.getProdServices(lb.getAllServers());
            } else {
                reachableServers = abstractDiscoveryProvider.getGrayServices(lb.getReachableServers(), requestHeaderVersion);
                allServers =  abstractDiscoveryProvider.getGrayServices(lb.getAllServers(), requestHeaderVersion);
            }

            /*
            if(StringUtils.isNotEmpty(requestHeaderVersion) &&
                    (reachableServers.size()==0 || allServers.size()==0)){
                reachableServers = abstractDiscoveryProvider.getProdServices(lb.getReachableServers());
                allServers = abstractDiscoveryProvider.getProdServices(lb.getAllServers());
            }*/

            if(StringUtils.isNotEmpty(requestHeaderVersion) &&
                    (allServers.size()==0)){
                reachableServers = abstractDiscoveryProvider.getProdServices(lb.getReachableServers());
                allServers = abstractDiscoveryProvider.getProdServices(lb.getAllServers());
            }
            // ZoneAwareLoadBalancer 会根据不同的区域找不同的对象
            // System.out.println ("=======================服务总数"+lb.getAllServers().size());
            // System.out.println("=======================真实服务总数"+lb.getReachableServers().size());
            // System.out.println("======================key名称"+key);
            // System.out.println("======================类名称"+lb.getClass().getSimpleName());
            // System.out.println("======================类名称"+lb.toString());
            // System.out.println("======================类名称"+((ZoneAwareLoadBalancer)lb).getName());
            // System.out.println("======================对象总数"+ SpringContextUtils.getBeans(ILoadBalancer.class).size());

            int upCount = reachableServers.size();
            int serverCount = allServers.size();

            /*
            if ((upCount == 0) || (serverCount == 0)) {
                log.warn("No up servers available from load balancer: " + lb);
                return null;
            }*/


            if (serverCount == 0) {
                log.warn("No up servers available from load balancer: " + lb);
                return null;
            }

            int nextServerIndex = incrementAndGetModulo(serverCount);
            server = allServers.get(nextServerIndex);

            if (server == null) {
                /* Transient. */
                Thread.yield();
                continue;
            }


            return (server);
           /* if (server.isAlive() && (server.isReadyToServe())) {
                return (server);
            }*/

            // Next.
            //server = null;
        }

        if (count >= 10) {
            log.warn("No available alive servers after 10 tries from load balancer: "
                    + lb);
        }
        return server;
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
