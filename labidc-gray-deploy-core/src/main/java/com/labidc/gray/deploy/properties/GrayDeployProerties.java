package com.labidc.gray.deploy.properties;

import com.labidc.gray.deploy.ribbon.GrayDeployRibbonRuleEnum;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @program: labidc-manager
 * @description: 自动配置文件
 * @author: ChenXingLiang
 * @date: 2018-11-09 00:04
 **/
@ConfigurationProperties("spring.gray.deploy")
@Component
@Data
public class GrayDeployProerties {
    /**
     * 负载均衡规则
     */
    private GrayDeployRibbonRuleEnum ribbonRule = GrayDeployRibbonRuleEnum.ROUND_ROBIN;

    /**
     * 启用重试策略
     */
    private Boolean retry = false;

    /**
     * 最长重试时间(毫秒) (当重试策略启用时有效)
     */
    private Long maxRetryMillis = 500L;

    /**
     * 当 header 中没有携带 version 读取当前服务 version 并传递给下一个服务
     */
    private Boolean readConfigVersion = false;

    /**
     * 默认调用服务版本号
     */
    private List<String> defaultServiceVersions = Collections.emptyList();


    public void setDefaultServiceVersions(List<String> defaultServiceVersions) {
        if (CollectionUtils.isEmpty(defaultServiceVersions)) {
            return;
        }

        List<String> list = new ArrayList<>();
        for (String defaultServiceVersion : defaultServiceVersions) {
            if (defaultServiceVersion != null) {
                String trim = defaultServiceVersion.toUpperCase().trim();
                if (!trim.isEmpty()) {
                    list.add(trim);
                }
            }
        }

        this.defaultServiceVersions = list;
    }
}
