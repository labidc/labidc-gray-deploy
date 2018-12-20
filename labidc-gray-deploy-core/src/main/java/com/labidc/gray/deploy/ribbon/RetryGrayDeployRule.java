package com.labidc.gray.deploy.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;

/**
 * @program: labidc-manager
 * @description: 先按照RoundRobinRule的策略获取服务,如果获取服务失败,则在指定时间内会进行重试,获取可用的服务;
 * 原代码就是包含数组合了 RoundRobinGrayDeployRule 的策略
 * @author: ChenXingLiang
 * @date: 2018-11-09 01:12
 **/
public class RetryGrayDeployRule extends AbstractLoadBalancerRule {



    IRule subRule = null;
    long maxRetryMillis = 500;


    public RetryGrayDeployRule() {
        this.subRule = new RoundRobinGrayDeployRule();
    }

    public RetryGrayDeployRule(IRule subRule) {
        this.subRule = (subRule != null) ? subRule : new RoundRobinRule();
    }

    public RetryGrayDeployRule(IRule subRule, long maxRetryMillis) {
        this.subRule = (subRule != null) ? subRule : new RoundRobinRule();
        this.maxRetryMillis = (maxRetryMillis > 0) ? maxRetryMillis : 500;
    }

    public void setRule(IRule subRule) {
        this.subRule = (subRule != null) ? subRule : new RoundRobinRule();
    }

    public IRule getRule() {
        return subRule;
    }

    public void setMaxRetryMillis(long maxRetryMillis) {
        if (maxRetryMillis > 0) {
            this.maxRetryMillis = maxRetryMillis;
        } else {
            this.maxRetryMillis = 500;
        }
    }

    public long getMaxRetryMillis() {
        return maxRetryMillis;
    }



    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(lb);
        subRule.setLoadBalancer(lb);
    }

    /*
     * Loop if necessary. Note that the time CAN be exceeded depending on the
     * subRule, because we're not spawning additional threads and returning
     * early.
     */
    public Server choose(ILoadBalancer lb, Object key) {
        long requestTime = System.currentTimeMillis();
        long deadline = requestTime + maxRetryMillis;

        Server answer = null;

        answer = subRule.choose(key);

        if (((answer == null) || (!answer.isAlive()))
                && (System.currentTimeMillis() < deadline)) {

            InterruptTask task = new InterruptTask(deadline
                    - System.currentTimeMillis());

            while (!Thread.interrupted()) {
                answer = subRule.choose(key);

                if (((answer == null) || (!answer.isAlive()))
                        && (System.currentTimeMillis() < deadline)) {
                    /* pause and retry hoping it's transient */
                    Thread.yield();
                } else {
                    break;
                }
            }

            task.cancel();
        }

        if ((answer == null) || (!answer.isAlive())) {
            return null;
        } else {
            return answer;
        }
    }

    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(), key);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {}
}
