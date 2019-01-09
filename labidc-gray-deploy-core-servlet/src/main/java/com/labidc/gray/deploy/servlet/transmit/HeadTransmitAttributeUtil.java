package com.labidc.gray.deploy.servlet.transmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class HeadTransmitAttributeUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeadTransmitAttributeUtil.class);
    private HeadTransmitAttributeUtil(){
    }

    public static String attributeObjectToString( String headerName, Object headerValueObject,HeadTransmitAttributeObjectTransform feignHeadTransmitAttributeObjectTransform){
        if (headerValueObject == null) {
            return null;
        }

        String headerValue = null;
        if (headerValueObject instanceof String) {
            headerValue = (String) headerValueObject;
        } else {
            Class<?> aClass = headerValueObject.getClass();
            if (aClass.isPrimitive() || isWrapClass(aClass)) {
                headerValue = headerValueObject.toString();
            }else if(feignHeadTransmitAttributeObjectTransform != null){
                try {
                    headerValue =feignHeadTransmitAttributeObjectTransform.transform(headerName,headerValueObject);
                }catch (Throwable e){
                    LOGGER.error("Attribute  Object To String  err : {}",e.getMessage());
                }
            }
        }

        return headerValue;
    }

    private static final String TYPE = "TYPE";

    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField(TYPE).get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

}
