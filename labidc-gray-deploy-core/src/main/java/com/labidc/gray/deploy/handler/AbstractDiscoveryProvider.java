package com.labidc.gray.deploy.handler;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @program: labidc-manager
 * @description: 注册发现服务提供者接口
 * @author: ChenXingLiang
 * @date: 2018-11-08 17:44
 **/
public abstract class AbstractDiscoveryProvider {

    /**
     * 日志控制器
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractDiscoveryProvider.class);
    /**
     * 当前调用者服务的实例
     */
    @Autowired
    protected Registration registration;

    /**
     * 设置日期格式,精确到毫秒
     */
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");

    /**
     * Servlet PostConstruct操作
     */
    @PostConstruct
    private void init(){
        logger.debug("时间："+df.format(new Date())+" 执行@PostConstruct修饰的 init()方法...");
    }


    /**
     * 获取元数据
     * @param server
     * @return
     */
    public abstract Map<String, String> getServerMetadata(Server server);


    /**
     * 根据当前服务对象的元数据字段获取到服务的版本号
     * @param server
     * @return
     * @throws Exception
     */
    public String getVersion(Server server)  {
        return this.getServerMetadata(server).get(GrayDeployConstant.VERSION);
    }

    /**
     * 获取当前请头的版本号
     * @return
     */
    public String getRequestHeaderVersion() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getHeader(GrayDeployConstant.VERSION);
    }

    /**
     * 返回生产服务
     * @param serverList
     * @return
     */
    public List<Server> getProdServices(List<Server> serverList){
       return serverList.stream().filter((item) -> StringUtils.isEmpty(this.getVersion(item))).collect(toList());
    }

    /**
     * 返指定版本号的回灰度服务
     * @param serverList
     * @return
     */
    public List<Server> getGrayServices(List<Server> serverList, String requestHeaderVersion) {
        return serverList.stream().filter((item) ->
                StringUtils.isNotEmpty(this.getVersion(item)) &&
                        this.getVersion(item).toUpperCase().trim().equals(requestHeaderVersion.toUpperCase().trim())
        ).collect(toList());
    }


    /**
     * 根据当前请求版本的头获取灰度服务
     * @param serverList
     * @return
     */
    public List<Server> getGrayServices(List<Server> serverList) {
        return this.getGrayServices(serverList, this.getRequestHeaderVersion());
    }
}
