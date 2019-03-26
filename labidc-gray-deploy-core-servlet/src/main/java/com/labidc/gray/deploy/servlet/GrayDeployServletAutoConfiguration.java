package com.labidc.gray.deploy.servlet;

import com.labidc.gray.deploy.handler.RequestSelfDataProvider;
import com.labidc.gray.deploy.handler.VersionProvider;
import com.labidc.gray.deploy.servlet.properties.GrayDeployTransmitProerties;
import com.labidc.gray.deploy.servlet.provider.ServletRequestSelfDataProvider;
import com.labidc.gray.deploy.servlet.provider.ServletVersionProvider;
import com.labidc.gray.deploy.servlet.transmit.HeadTransmit;
import com.labidc.gray.deploy.servlet.transmit.HeadTransmitObjectTransform;
import com.labidc.gray.deploy.servlet.transmit.HeadTransmitServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * labidc-manager
 * 自动配置对象
 *
 * @author ChenXingLiang
 * @date 2018-11-08 20:46
 **/

@ComponentScan(value = "com.labidc.gray.deploy.servlet")
@Configuration
public class GrayDeployServletAutoConfiguration {

    @Autowired(required = false)
    private HeadTransmitObjectTransform headTransmitObjectTransform;

    @Bean
    @ConditionalOnMissingBean(HeadTransmit.class)
    public HeadTransmitServlet transmitServlet(GrayDeployTransmitProerties grayDeployTransmitProerties) {
        return new HeadTransmitServlet(grayDeployTransmitProerties, headTransmitObjectTransform);
    }

    @Bean
    @ConditionalOnMissingBean(VersionProvider.class)
    public VersionProvider versionProvider() {
        return new ServletVersionProvider();
    }

    @Bean
    @ConditionalOnMissingBean(RequestSelfDataProvider.class)
    public RequestSelfDataProvider requestSelfDataProvider() {
        return new ServletRequestSelfDataProvider();
    }
}
