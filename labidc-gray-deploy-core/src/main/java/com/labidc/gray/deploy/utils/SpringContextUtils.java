package com.labidc.gray.deploy.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Author：Mr.X
 * Date：2017/11/8 10:00
 * Description：
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    /**
     * 上下文对象实例
     */
    private static ApplicationContext staticApplicationContext;

    /**
     * 获取applicationContext
     */
    private static ApplicationContext getStaticApplicationContext() {
        return staticApplicationContext;
    }

    /**
     * 设置applicationContext
     */
    private static void setStaticApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtils.staticApplicationContext = applicationContext;
    }

    /**
     * 通过name获取 Bean.
     */
    public static Object getBean(String name) {
        return getStaticApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     */
    public static <T> T getBean(Class<T> clazz) {

        return getStaticApplicationContext().getBean(clazz);
    }

    /**
     * 通过class获取Bean.
     */
    public static <T> Map<String, T> getBeans(Class<T> clazz) {

        return getStaticApplicationContext().getBeansOfType(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getStaticApplicationContext().getBean(name, clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtils.setStaticApplicationContext(applicationContext);
    }
}