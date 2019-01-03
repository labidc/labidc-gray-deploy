package com.labidc.gray.deploy.gateway;

import com.labidc.gray.deploy.handler.AbstractVersionProvider;
import org.springframework.stereotype.Component;

/**
 * @program: servicedemo
 * @description: gateway版本提供者
 * @author: ChenXingLiang
 * @date: 2019-01-03 13:29
 **/
@Component(value = "VersionProvider")
public class GateWayVersionProvider extends AbstractVersionProvider {

    public static final ThreadLocal<String> grayDeployThreadLocal = new ThreadLocal<String>();
    @Override
    public String getRequestHeaderVersion() {
        return grayDeployThreadLocal.get();
    }
}
