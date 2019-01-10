package com.labidc.gray.deploy.ribbon.rules.predicate;

import com.labidc.gray.deploy.ribbon.rules.ZoneAvoidanceGrayDeployRule;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicDoubleProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.loadbalancer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * @program: labidc-manager
 * @description: TODO
 * @author: ChenXingLiang
 * @date: 2018-11-13 21:46
 **/
public class ZoneAvoidanceGrayDeployPredicate extends AbstractServerPredicate {

    private volatile DynamicDoubleProperty triggeringLoad = new DynamicDoubleProperty("ZoneAwareNIWSDiscoveryLoadBalancer.triggeringLoadPerServerThreshold", 0.2d);

    private volatile DynamicDoubleProperty triggeringBlackoutPercentage = new DynamicDoubleProperty("ZoneAwareNIWSDiscoveryLoadBalancer.avoidZoneWithBlackoutPercetage", 0.99999d);

    private static final Logger logger = LoggerFactory.getLogger(ZoneAvoidancePredicate.class);

    private static final DynamicBooleanProperty ENABLED = DynamicPropertyFactory
            .getInstance().getBooleanProperty(
                    "niws.loadbalancer.zoneAvoidanceRule.enabled", true);


    public ZoneAvoidanceGrayDeployPredicate(IRule rule, IClientConfig clientConfig) {
        super(rule, clientConfig);
        initDynamicProperties(clientConfig);
    }

    public ZoneAvoidanceGrayDeployPredicate(LoadBalancerStats lbStats,
                                  IClientConfig clientConfig) {
        super(lbStats, clientConfig);
        initDynamicProperties(clientConfig);
    }

    public ZoneAvoidanceGrayDeployPredicate(IRule rule) {
        super(rule);
    }

    private void initDynamicProperties(IClientConfig clientConfig) {
        if (clientConfig != null) {
            triggeringLoad = DynamicPropertyFactory.getInstance().getDoubleProperty(
                    "ZoneAwareNIWSDiscoveryLoadBalancer." + clientConfig.getClientName() + ".triggeringLoadPerServerThreshold", 0.2d);

            triggeringBlackoutPercentage = DynamicPropertyFactory.getInstance().getDoubleProperty(
                    "ZoneAwareNIWSDiscoveryLoadBalancer." + clientConfig.getClientName() + ".avoidZoneWithBlackoutPercetage", 0.99999d);
        }

    }

    @Override
    public boolean apply(@Nullable PredicateKey input) {
        if (!ENABLED.get()) {
            return true;
        }
        String serverZone = input.getServer().getZone();
        if (serverZone == null) {
            // there is no zone information from the server, we do not want to filter
            // out this server
            return true;
        }
        LoadBalancerStats lbStats = getLBStats();
        if (lbStats == null) {
            // no stats available, do not filter
            return true;
        }
        if (lbStats.getAvailableZones().size() <= 1) {
            // only one zone is available, do not filter
            return true;
        }
        Map<String, ZoneSnapshot> zoneSnapshot = ZoneAvoidanceGrayDeployRule.createSnapshot(lbStats);
        if (!zoneSnapshot.keySet().contains(serverZone)) {
            // The server zone is unknown to the load balancer, do not filter it out
            return true;
        }
        logger.debug("Zone snapshots: {}", zoneSnapshot);
        Set<String> availableZones = ZoneAvoidanceRule.getAvailableZones(zoneSnapshot, triggeringLoad.get(), triggeringBlackoutPercentage.get());
        logger.debug("Available zones: {}", availableZones);
        if (availableZones != null) {
            return availableZones.contains(input.getServer().getZone());
        } else {
            return false;
        }
    }
}
