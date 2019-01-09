package com.labidc.gray.deploy.servlet.feign;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public interface FeignHeadTransmitAttributeObjectTransform {
    /**
     * 对象转换为String
     */
    String transform(String attributeName,Object attributeValue);
}
