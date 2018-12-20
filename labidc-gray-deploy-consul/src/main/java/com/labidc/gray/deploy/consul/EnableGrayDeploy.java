package com.labidc.gray.deploy.consul;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动灰度发布
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AutoConsulGrayDeployConfiguration.class})
public @interface EnableGrayDeploy { }
