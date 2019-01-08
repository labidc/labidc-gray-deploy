package com.labidc.gray.deploy.ribbon;

/**
 * @program: servicedemo
 * @description: TODO
 * @author: ChenXingLiang
 * @date: 2019-01-07 10:50
 **/
public enum GrayDeployRibbonRuleEnum {
    /**
     * 根据平均响应时间计算所有服务的权重,响应时间越快,服务权重越大,被选中的机率越高;
     */
    WEIGHTED_RESPONSE_TIME,
    /**
     * 随机
     */
    RANDOM,
    /**
     * 轮询
     */
    ROUND_ROBIN,
    /**
     * 会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务, 以及并发的连接数量
     * 超过阈值的服务,然后对剩余的服务列表按照轮询策略进行访问;
     */
    AVAILABILITY_FILTERING,
    /**
     * 会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务,然后选择一个并发量最小的服务;
     * 实际上是继承的轮询模式，再做的一个升级
     */
    BEST_AVAILABLE,
    /**
     * 复合判断server所在区域的性能和server的可用性选择服务器;
     */
    ZONE_AVOIDANCE,

    ;
}
