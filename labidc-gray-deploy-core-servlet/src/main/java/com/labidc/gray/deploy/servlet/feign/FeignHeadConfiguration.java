package com.labidc.gray.deploy.servlet.feign;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.labidc.gray.deploy.servlet.properties.GrayDeployTransmitProerties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @program: labidc-manager
 * @description: Feign拦截器配置，传递指定请求头
 * @author: ChenXingLiang
 * @date: 2018-11-08 16:38
 **/
@Configuration
public class FeignHeadConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeignHeadConfiguration.class);

    @Autowired(required = false)
    private FeignHeadTransmitAttributeObjectTransform feignHeadTransmitAttributeObjectTransform;

    /**
     * 传递指定请求头
     *
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor(GrayDeployTransmitProerties grayDeployTransmitProerties) {
        return requestTemplate -> {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                // 将请求头中version转发到下游服务
                String version = request.getHeader(GrayDeployConstant.VERSION);
                if (!StringUtils.isEmpty(version)) {
                    requestTemplate.header(GrayDeployConstant.VERSION, version);
                }

                // 将请求头中指定headers的转发到下游服务
                List<String> headers = grayDeployTransmitProerties.getHeaders();
                if (!CollectionUtils.isEmpty(headers)) {
                    for (String header : headers) {
                        this.setHeader(requestTemplate, header, request.getHeader(header));
                    }
                }

                // 将请求头中指定attributes的转发到下游服务
                List<String> attributes = grayDeployTransmitProerties.getAttributes();
                if (!CollectionUtils.isEmpty(attributes)) {
                    for (String attribute : attributes) {
                        this.setHeader(requestTemplate, attribute, request.getAttribute(attribute));
                    }
                }
            }
        };
    }

    private static final String TYPE = "TYPE";

    public boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField(TYPE).get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    private void setHeader(RequestTemplate requestTemplate, String headerName, Object headerValueObject) {
        if (headerValueObject == null) {
            return;
        }

        String headerValue = null;
        if (headerValueObject instanceof String) {
            headerValue = (String) headerValueObject;
        } else {
            Class<?> aClass = headerValueObject.getClass();
            if (aClass.isPrimitive() || this.isWrapClass(aClass)) {
                headerValue = headerValueObject.toString();
            }else if(feignHeadTransmitAttributeObjectTransform != null){
                try {
                    headerValue =feignHeadTransmitAttributeObjectTransform.transform(headerName,headerValueObject);
                }catch (Throwable e){
                    LOGGER.error("Attribute  Object To String  err : {}",e.getMessage());
                }
            }
        }


        this.setHeader(requestTemplate, headerName, headerValue);
    }

    private void setHeader(RequestTemplate requestTemplate, String headerName, String headerValue) {
        if (StringUtils.isEmpty(headerValue) || StringUtils.isEmpty(headerName)) {
            return;
        }

        Map<String, Collection<String>> headers = requestTemplate.headers();
        if (!CollectionUtils.isEmpty(headers.get(headerName))) {
            return;
        }

        requestTemplate.header(headerName, headerValue);
    }
}
