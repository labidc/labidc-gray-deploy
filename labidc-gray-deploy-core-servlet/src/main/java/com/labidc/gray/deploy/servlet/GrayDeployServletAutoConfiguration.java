package com.labidc.gray.deploy.servlet;

import com.labidc.gray.deploy.servlet.properties.GrayDeployTransmitProerties;
import com.labidc.gray.deploy.servlet.transmit.HeadTransmit;
import com.labidc.gray.deploy.servlet.transmit.HeadTransmitAttributeObjectTransform;
import com.labidc.gray.deploy.servlet.transmit.HeadTransmitServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @program: labidc-manager
 * @description: 自动配置对象
 * @author: ChenXingLiang
 * @date: 2018-11-08 20:46
 **/

@ComponentScan(value = "com.labidc.gray.deploy.servlet")
@Configuration
public class GrayDeployServletAutoConfiguration {

    @Autowired(required = false)
    private HeadTransmitAttributeObjectTransform headTransmitAttributeObjectTransform;

    @Bean
    @ConditionalOnMissingBean(HeadTransmit.class)
    public HeadTransmitServlet transmitServlet(GrayDeployTransmitProerties grayDeployTransmitProerties){
        return new HeadTransmitServlet(grayDeployTransmitProerties,headTransmitAttributeObjectTransform);
    }
}
