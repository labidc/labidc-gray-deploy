package com.labidc.gray.deploy.handler;

import com.labidc.gray.deploy.constant.GrayDeployConstant;
import com.netflix.loadbalancer.Server;
import lombok.extern.java.Log;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.Registration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collections;
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
@Log
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
    private void init() {
        logger.debug("时间：" + df.format(new Date()) + " 执行@PostConstruct修饰的 init()方法...");
    }


    @Resource(name = "VersionProvider")
    @Autowired
    protected AbstractVersionProvider abstractVersionProvider;


    /**
     * 获取元数据
     *
     * @param server
     * @return
     */
    public abstract Map<String, String> getServerMetadata(Server server);


    /**
     * 获取当前服务的版本号
     *
     * @param
     * @return 如果不存在版本号，则为null
     */
    public abstract String getCurrentVersion();

    /**
     * 根据当前服务对象的元数据字段获取到服务的版本号
     *
     * @param server
     * @return
     * @throws Exception
     */
    public String getVersion(Server server) {
        return this.getServerMetadata(server).get(GrayDeployConstant.VERSION);
    }


    /**
     * 获取版本号
     *
     * @return
     */
    public String getRequestHeaderVersion() {
        return abstractVersionProvider.getRequestHeaderVersion();
    }


    /**
     * 返回生产服务
     *
     * @param serverList
     * @return
     */
    public List<Server> getProdServices(List<Server> serverList) {
        return serverList.stream().filter((item) -> StringUtils.isEmpty(this.getVersion(item))).collect(toList());
    }

    /**
     * 返指定版本号的回灰度服务
     *
     * @param serverList
     * @return
     */
    public List<Server> getGrayServices(List<Server> serverList, String requestHeaderVersion) {
        if (StringUtils.isBlank(requestHeaderVersion)) {
            return Collections.emptyList();
        }
        return serverList.stream().filter((item) ->
                StringUtils.isNotEmpty(this.getVersion(item)) &&
                        this.getVersion(item).toUpperCase().trim().equals(requestHeaderVersion.toUpperCase().trim())
        ).collect(toList());
    }

    /**
     * 返回服务
     * 1. 如果有 requestHeaderVersion 返回 <灰度版本>  否则 返回 <正式版本>
     * 2. 如果<灰度版本>不存在 则返回 <正式版本>
     *
     * @param serverList
     * @param requestHeaderVersion
     * @return
     */
    public List<Server> getServicesAuto(List<Server> serverList, String requestHeaderVersion) {
        if (StringUtils.isNotBlank(requestHeaderVersion)) {
            List<Server> grayServices = this.getGrayServices(serverList, requestHeaderVersion);
            if (CollectionUtils.isNotEmpty(grayServices)
                    /*&& grayServices.stream().anyMatch(Server::isAlive)*/) {
                return grayServices;
            }
        }

        return this.getProdServices(serverList);
    }


    /**
     * 根据当前请求版本的头获取灰度服务
     *
     * @param serverList
     * @return
     */
    public List<Server> getGrayServices(List<Server> serverList) {
        return this.getGrayServices(serverList, this.getRequestHeaderVersion());
    }
}
