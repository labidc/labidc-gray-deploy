package com.labidc.gray.deploy.eureka;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动灰度发布
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AutoEurekaDiscoveryConfiguration.class})
public @interface EnableGrayDeploy {
}
