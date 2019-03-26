package com.labidc.gray.deploy.servlet.transmit;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public interface HeadTransmitObjectTransform {
    /**
     * 对象转换为String
     */
    String transform(String attributeName, Object attributeValue);
}
