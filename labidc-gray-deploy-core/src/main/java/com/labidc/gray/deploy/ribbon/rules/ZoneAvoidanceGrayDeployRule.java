package com.labidc.gray.deploy.ribbon.rules;

import com.labidc.gray.deploy.ribbon.rules.predicate.AvailabilityGrayDeployPredicate;
import com.labidc.gray.deploy.ribbon.rules.predicate.ZoneAvoidanceGrayDeployPredicate;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.ZoneSnapshot;

import java.util.*;

/**
 * @program: labidc-manager
 * @description: 默认规则,复合判断server所在区域的性能和server的可用性选择服务器;
 * @author: ChenXingLiang
 * @date: 2018-11-09 01:17
 **/
public class ZoneAvoidanceGrayDeployRule  extends PredicateBasedGrayDeployRule  {

    private static final Random RANDOM = new Random();

    private CompositePredicate compositePredicate;

    public ZoneAvoidanceGrayDeployRule() {
        super();
        ZoneAvoidanceGrayDeployPredicate zonePredicate = new ZoneAvoidanceGrayDeployPredicate(this);
        AvailabilityGrayDeployPredicate availabilityPredicate = new AvailabilityGrayDeployPredicate(this);
        compositePredicate = createCompositePredicate(zonePredicate, availabilityPredicate);
    }

    private CompositePredicate createCompositePredicate(ZoneAvoidanceGrayDeployPredicate p1, AvailabilityGrayDeployPredicate p2) {
        return CompositePredicate.withPredicates(p1, p2)
                .addFallbackPredicate(p2)
                .addFallbackPredicate(AbstractServerPredicate.alwaysTrue())
                .build();

    }


    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        ZoneAvoidanceGrayDeployPredicate zonePredicate = new ZoneAvoidanceGrayDeployPredicate(this, clientConfig);
        AvailabilityGrayDeployPredicate availabilityPredicate = new AvailabilityGrayDeployPredicate(this, clientConfig);
        compositePredicate = createCompositePredicate(zonePredicate, availabilityPredicate);
    }

    public static Map<String, ZoneSnapshot> createSnapshot(LoadBalancerStats lbStats) {
        Map<String, ZoneSnapshot> map = new HashMap<String, ZoneSnapshot>();
        for (String zone : lbStats.getAvailableZones()) {
            ZoneSnapshot snapshot = lbStats.getZoneSnapshot(zone);
            map.put(zone, snapshot);
        }
        return map;
    }

    static String randomChooseZone(Map<String, ZoneSnapshot> snapshot,
                                   Set<String> chooseFrom) {
        if (chooseFrom == null || chooseFrom.size() == 0) {
            return null;
        }
        String selectedZone = chooseFrom.iterator().next();
        if (chooseFrom.size() == 1) {
            return selectedZone;
        }
        int totalServerCount = 0;
        for (String zone : chooseFrom) {
            totalServerCount += snapshot.get(zone).getInstanceCount();
        }
        int index = RANDOM.nextInt(totalServerCount) + 1;
        int sum = 0;
        for (String zone : chooseFrom) {
            sum += snapshot.get(zone).getInstanceCount();
            if (index <= sum) {
                selectedZone = zone;
                break;
            }
        }
        return selectedZone;
    }

    public static Set<String> getAvailableZones(
            Map<String, ZoneSnapshot> snapshot, double triggeringLoad,
            double triggeringBlackoutPercentage) {
        if (snapshot.isEmpty()) {
            return null;
        }
        Set<String> availableZones = new HashSet<String>(snapshot.keySet());
        if (availableZones.size() == 1) {
            return availableZones;
        }
        Set<String> worstZones = new HashSet<String>();
        double maxLoadPerServer = 0;
        boolean limitedZoneAvailability = false;

        for (Map.Entry<String, ZoneSnapshot> zoneEntry : snapshot.entrySet()) {
            String zone = zoneEntry.getKey();
            ZoneSnapshot zoneSnapshot = zoneEntry.getValue();
            int instanceCount = zoneSnapshot.getInstanceCount();
            if (instanceCount == 0) {
                availableZones.remove(zone);
                limitedZoneAvailability = true;
            } else {
                double loadPerServer = zoneSnapshot.getLoadPerServer();
                if (((double) zoneSnapshot.getCircuitTrippedCount())
                        / instanceCount >= triggeringBlackoutPercentage
                        || loadPerServer < 0) {
                    availableZones.remove(zone);
                    limitedZoneAvailability = true;
                } else {
                    if (Math.abs(loadPerServer - maxLoadPerServer) < 0.000001d) {
                        // they are the same considering double calculation
                        // round error
                        worstZones.add(zone);
                    } else if (loadPerServer > maxLoadPerServer) {
                        maxLoadPerServer = loadPerServer;
                        worstZones.clear();
                        worstZones.add(zone);
                    }
                }
            }
        }

        if (maxLoadPerServer < triggeringLoad && !limitedZoneAvailability) {
            // zone override is not needed here
            return availableZones;
        }
        String zoneToAvoid = randomChooseZone(snapshot, worstZones);
        if (zoneToAvoid != null) {
            availableZones.remove(zoneToAvoid);
        }
        return availableZones;

    }

    public static Set<String> getAvailableZones(LoadBalancerStats lbStats,
                                                double triggeringLoad, double triggeringBlackoutPercentage) {
        if (lbStats == null) {
            return null;
        }
        Map<String, ZoneSnapshot> snapshot = createSnapshot(lbStats);
        return getAvailableZones(snapshot, triggeringLoad,
                triggeringBlackoutPercentage);
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return compositePredicate;
    }
}
