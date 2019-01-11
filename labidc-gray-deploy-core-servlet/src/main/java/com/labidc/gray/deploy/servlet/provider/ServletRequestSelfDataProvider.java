package com.labidc.gray.deploy.servlet.provider;

import com.labidc.gray.deploy.handler.RequestSelfDataProvider;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class ServletRequestSelfDataProvider implements RequestSelfDataProvider {
    @Override
    public Map<String, Object> getRequestSelfData() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            //System.out.println("==================================11111线程ID"+ Thread.currentThread());
            return null;
        }

        Map<String, Object> map = new HashMap<>();

        HttpServletRequest request = requestAttributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            map.put(headerName,request.getHeader(headerName));
        }
        return map;
    }
}
