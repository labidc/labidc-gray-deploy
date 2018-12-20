package com.labidc.gray.deploy.feign;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

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
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {

                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String value = request.getHeader(name);
                        // 遍历请求头里面的属性字段，将logId和token添加到新的请求头中转发到下游服务
                        if (GrayDeployConstant.VERSION.equalsIgnoreCase(name)) {
                            logger.debug("======================获取到指定请求头：");
                            requestTemplate.header(name, value);
                            break;
                        }
                    }
                }
            }
        };
    }
}
