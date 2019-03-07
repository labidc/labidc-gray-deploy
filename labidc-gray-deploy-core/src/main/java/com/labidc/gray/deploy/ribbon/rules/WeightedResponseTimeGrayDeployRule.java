package com.labidc.gray.deploy.ribbon.rules;

import com.labidc.gray.deploy.filter.ServerFilter;
import com.labidc.gray.deploy.utils.SpringContextUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.loadbalancer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * labidc-manager
 * 根据平均响应时间计算所有服务的权重, 响应时间越快, 服务权重越大, 被选中的机率越高;
 * 继承 RoundRobinGrayDeployRule 对象，原继承 RoundRobinRule
 *
 * @author ChenXingLiang
 * @date 2018-11-08 16:48
 **/
public class WeightedResponseTimeGrayDeployRule extends RoundRobinGrayDeployRule {

    private static final IClientConfigKey<Integer> WEIGHT_TASK_TIMER_INTERVAL_CONFIG_KEY = new IClientConfigKey<Integer>() {
        @Override
        public String key() {
            return "ServerWeightTaskTimerInterval";
        }

        @Override
        public String toString() {
            return key();
        }

        @Override
        public Class<Integer> type() {
            return Integer.class;
        }
    };
    private static final int DEFAULT_TIMER_INTERVAL = 30 * 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(WeightedResponseTimeRule.class);
    private final Random random = new Random();
    private Timer serverWeightTimer = null;
    private AtomicBoolean serverWeightAssignmentInProgress = new AtomicBoolean(false);
    String name = "unknown";
    @Autowired
    private ServerFilter serverFilter;
    private int serverWeightTaskTimerInterval = DEFAULT_TIMER_INTERVAL;
    // holds the accumulated weight from index 0 to current index
    // for example, element at index 2 holds the sum of weight of servers from 0 to 2
    private volatile List<Double> accumulatedWeights = new ArrayList<>();

    public WeightedResponseTimeGrayDeployRule() {
        super();
    }

    public WeightedResponseTimeGrayDeployRule(ILoadBalancer lb) {
        super(lb);
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(lb);
        if (lb instanceof BaseLoadBalancer) {
            name = ((BaseLoadBalancer) lb).getName();
        }
        initialize(lb);
    }

    private void initialize(ILoadBalancer lb) {
        if (serverWeightTimer != null) {
            serverWeightTimer.cancel();
            //serverWeightTimer.shutdown();
        }
        serverWeightTimer = new Timer("NFLoadBalancer-serverWeightTimer-" + name, true);

        serverWeightTimer.schedule(new WeightedResponseTimeGrayDeployRule.DynamicServerWeightTask(), 0,
                serverWeightTaskTimerInterval);
        // do a initial run
        WeightedResponseTimeGrayDeployRule.ServerWeight sw = new WeightedResponseTimeGrayDeployRule.ServerWeight();
        sw.maintainWeights();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Stopping NFLoadBalancer-serverWeightTimer-" + name);
            serverWeightTimer.cancel();
            //serverWeightTimer.shutdown();
        }));
    }

    public void shutdown() {
        if (serverWeightTimer != null) {
            LOGGER.info("Stopping NFLoadBalancer-serverWeightTimer-" + name);
            serverWeightTimer.cancel();
            //serverWeightTimer.shutdown();
        }
    }

    List<Double> getAccumulatedWeights() {
        return Collections.unmodifiableList(accumulatedWeights);
    }

    @Override
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }

        if (this.serverFilter == null) {
            this.serverFilter = SpringContextUtils.getBean(ServerFilter.class);
        }


        Server server = null;
        while (server == null) {
            // get hold of the current reference in case it is changed from the other thread
            List<Double> currentWeights = accumulatedWeights;
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> allList = serverFilter.getServicesAuto(getLoadBalancer().getAllServers());

            int serverCount = allList.size();

            if (serverCount == 0) {
                return null;
            }

            int serverIndex = 0;

            // last one in the list is the sum of all weights
            double maxTotalWeight = currentWeights.isEmpty() ? 0 : currentWeights.get(currentWeights.size() - 1);
            // No server has been hit yet and total weight is not initialized
            // fallback to use round robin
            if (maxTotalWeight < 0.001d || serverCount != currentWeights.size()) {
                server = super.choose(getLoadBalancer(), key);
                if (server == null) {
                    return server;
                }
            } else {
                // generate a random weight between 0 (inclusive) to maxTotalWeight (exclusive)
                double randomWeight = random.nextDouble() * maxTotalWeight;
                // pick the server index based on the randomIndex
                int n = 0;
                for (Double d : currentWeights) {
                    if (d >= randomWeight) {
                        serverIndex = n;
                        break;
                    } else {
                        n++;
                    }
                }

                server = allList.get(serverIndex);
            }

            if (server == null) {
                /* Transient. */
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            // Next.
            server = null;
        }
        return server;
    }

    void setWeights(List<Double> weights) {
        this.accumulatedWeights = weights;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        super.initWithNiwsConfig(clientConfig);
        serverWeightTaskTimerInterval = clientConfig.get(WEIGHT_TASK_TIMER_INTERVAL_CONFIG_KEY, DEFAULT_TIMER_INTERVAL);
    }

    class DynamicServerWeightTask extends TimerTask {
        @Override
        public void run() {
            try {
                WeightedResponseTimeGrayDeployRule.ServerWeight serverWeight = new WeightedResponseTimeGrayDeployRule.ServerWeight();
                serverWeight.maintainWeights();
            } catch (Exception e) {
                LOGGER.error("Error running DynamicServerWeightTask for {}", name, e);
            }
        }
    }

    // 权重管理
    class ServerWeight {

        public void maintainWeights() {
            ILoadBalancer lb = getLoadBalancer();
            if (lb == null) {
                return;
            }

            if (!serverWeightAssignmentInProgress.compareAndSet(false, true)) {
                return;
            }

            try {
                LOGGER.info("Weight adjusting job started");
                AbstractLoadBalancer nlb = (AbstractLoadBalancer) lb;
                LoadBalancerStats stats = nlb.getLoadBalancerStats();
                if (stats == null) {
                    // no statistics, nothing to do
                    return;
                }
                double totalResponseTime = 0;


                // find maximal 95% response time
                for (Server server : nlb.getAllServers()) {
                    // this will automatically load the stats if not in cache
                    ServerStats ss = stats.getSingleServerStat(server);
                    totalResponseTime += ss.getResponseTimeAvg();
                }
                // weight for each server is (sum of responseTime of all servers - responseTime)
                // so that the longer the response time, the less the weight and the less likely to be chosen
                Double weightSoFar = 0.0;

                // create new list and hot swap the reference
                List<Double> finalWeights = new ArrayList<>();
                for (Server server : nlb.getAllServers()) {
                    ServerStats ss = stats.getSingleServerStat(server);
                    double weight = totalResponseTime - ss.getResponseTimeAvg();
                    weightSoFar += weight;
                    finalWeights.add(weightSoFar);
                }
                setWeights(finalWeights);
            } catch (Exception e) {
                LOGGER.error("Error calculating server weights", e);
            } finally {
                serverWeightAssignmentInProgress.set(false);
            }

        }
    }

}
