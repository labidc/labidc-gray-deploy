package com.labidc.gray.deploy.servlet;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.labidc.gray.deploy.handler.AbstractVersionProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: servicedemo
 * @description: servlet 版本提供者
 * @author: ChenXingLiang
 * @date: 2019-01-03 13:15
 **/
@Component(value = "VersionProvider")
public class ServletVersionProvider  extends AbstractVersionProvider {

    @Override
    public String getRequestHeaderVersion() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null){
            //System.out.println("==================================11111线程ID"+ Thread.currentThread());
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getHeader(GrayDeployConstant.VERSION);
    }
}
