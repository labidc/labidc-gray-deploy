package com.labidc.gray.deploy.zipkin;


import brave.Span;
import brave.Tracer;
import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.labidc.gray.deploy.handler.AbstractDiscoveryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.instrument.web.TraceWebServletAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @program: servicedemo
 * @description: TODO
 * @author: ChenXingLiang
 * @date: 2019-01-03 17:44
 **/
@Component
@Order(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1)
public class DelegatingGrayDeployFilter implements Filter {

    /**
     * 跟踪对象
     */
    private final Tracer tracer;

    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * 应用程序上下文
     */
    private ApplicationContext applicationContext;

    /**
     * 构造函数
     * @param tracer
     */
    public DelegatingGrayDeployFilter(Tracer tracer) {
        this.tracer = tracer;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // HttpServletResponse httpServletResponse =  (HttpServletResponse) response;
        HttpServletRequest httpServletRequest =  (HttpServletRequest) request;

        Span currentSpan = this.tracer.currentSpan();
        if (currentSpan == null || StringUtils.isEmpty(httpServletRequest.getHeader(GrayDeployConstant.VERSION))) {
            chain.doFilter(request, response);
            return;
        }

        AbstractDiscoveryProvider abstractDiscoveryProvider = this.applicationContext.getBean("DiscoveryProvider", AbstractDiscoveryProvider.class);
        String deployVersion = " >> " + this.serviceName + "_" + (abstractDiscoveryProvider.getCurrentVersion() == null ? "releases" : abstractDiscoveryProvider.getCurrentVersion());
        // 加入调用链，把版本号tag打上去
        currentSpan.tag(GrayDeployConstant.VERSION, deployVersion);
        //System.out.println("当前服务版本："+GrayDeployConstant.VERSION+"===="+deployVersion);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
