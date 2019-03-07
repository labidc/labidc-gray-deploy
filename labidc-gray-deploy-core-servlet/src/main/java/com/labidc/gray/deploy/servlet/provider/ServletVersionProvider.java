package com.labidc.gray.deploy.servlet.provider;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.labidc.gray.deploy.handler.VersionProvider;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @program: servicedemo
 * @description: servlet 版本提供者
 * @author: ChenXingLiang
 * @date: 2019-01-03 13:15
 **/
public class ServletVersionProvider implements VersionProvider {
    private static final String VERSION_HEADER_SPLIT = ",";

    @Override
    public List<String> getRequestHeaderVersions() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            //System.out.println("==================================11111线程ID"+ Thread.currentThread());
            return Collections.emptyList();
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String headers = request.getHeader(GrayDeployConstant.VERSION);
        if (!StringUtils.isEmpty(headers)) {
            return Collections.emptyList();
        }

        List<String> versionList = new ArrayList<>();
        for (String version : headers.split(VERSION_HEADER_SPLIT)) {
            String trim = version.toLowerCase().trim();
            if (!trim.isEmpty()) {
                versionList.add(trim);
            }
        }
        return versionList;
    }


}
