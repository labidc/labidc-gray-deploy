package com.labidc.gray.deploy.handler;


import java.util.List;

/**
 * @program: servicedemo
 * @description: 版本提供者
 * @author: ChenXingLiang
 * @date: 2019-01-03 13:19
 **/
public interface VersionProvider {

    /**
     * 获取版本号  需要大写去头尾空
     * @return
     */
    List<String> getRequestHeaderVersions();

}
