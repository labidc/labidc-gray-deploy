package com.labidc.gray.deploy.ribbon;

import com.netflix.loadbalancer.Server;

/**
 * @program: labidc-manager
 * @description: 灰度发布规则接口
 * @author: ChenXingLiang
 * @date: 2018-11-09 02:23
 **/
public interface IGrayDeployRule {
    /**
     * 负责调用原对象父类
     * @param key
     * @return
     */
    Server superChoose(Object key);
}
