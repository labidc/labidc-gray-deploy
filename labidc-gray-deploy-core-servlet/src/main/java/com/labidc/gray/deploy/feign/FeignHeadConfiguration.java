package com.labidc.gray.deploy.feign;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: labidc-manager
 * @description: Feign拦截器配置，传递指定请求头
 * @author: ChenXingLiang
 * @date: 2018-11-08 16:38
 **/
@Configuration
public class FeignHeadConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(FeignHeadConfiguration.class);

    /**
     * 传递指定请求头
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();

                // 将请求头中version转发到下游服务
                String version = request.getHeader(GrayDeployConstant.VERSION);
                if(!StringUtils.isEmpty(version)){
                    requestTemplate.header(GrayDeployConstant.VERSION, version);
                }
            }
        };
    }
}
