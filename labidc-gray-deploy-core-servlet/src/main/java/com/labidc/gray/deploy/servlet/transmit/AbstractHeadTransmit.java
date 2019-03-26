package com.labidc.gray.deploy.servlet.transmit;

import com.labidc.gray.deploy.servlet.properties.GrayDeployTransmitProerties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public abstract class AbstractHeadTransmit<S, N> implements HeadTransmit<S, N> {


    private GrayDeployTransmitProerties grayDeployTransmitProerties;
    private HeadTransmitObjectTransform headTransmitObjectTransform;

    public AbstractHeadTransmit(GrayDeployTransmitProerties grayDeployTransmitProerties,
                                HeadTransmitObjectTransform headTransmitObjectTransform) {
        this.grayDeployTransmitProerties = grayDeployTransmitProerties;
        this.headTransmitObjectTransform = headTransmitObjectTransform;
    }


    /**
     * 获取需要传递的header的值
     */
    protected abstract String getHeader(S transmitSource, String header);

    /**
     * 获取需要传递的attribute的值
     */
    protected abstract Object getAttribute(S transmitSource, String attribute);

    /**
     * 获取需要传递的paramter的值
     */
    protected abstract Object getParamter(S transmitSource, String paramter);

    /**
     * 查看本次请求是否已经有当前header
     */
    protected abstract boolean nextRequestHasHeader(N needTransmit, String header);

    /**
     * 向本次请求设置header
     */
    protected abstract void nextRequestSetHeader(N needTransmit, String headerName, String headerValue);

    /**
     * 获取需要传递的attribute的值（string）
     */
    protected String getAttributeString(S transmitSource, String attribute) {
        return HeadTransmitAttributeUtil.objectToString(attribute, this.getAttribute(transmitSource, attribute), this.headTransmitObjectTransform);
    }

    protected String getParamterString(S transmitSource, String paramter) {
        return HeadTransmitAttributeUtil.objectToString(paramter, this.getParamter(transmitSource, paramter), this.headTransmitObjectTransform);
    }


    protected void transmit(N needTransmit, List<String> names, Function<String, String> valueFunction) {
        if (CollectionUtils.isEmpty(names)) {
            return;
        }

        for (String name : names) {
            if (this.nextRequestHasHeader(needTransmit, name)) {
                continue;
            }

            this.setHeader(needTransmit, name, valueFunction.apply(name));
        }
    }

    /**
     * 设置传输的 header
     */
    @Override
    public void transmit(S transmitSource, N needTransmit) {
        // 将请求头中指定headers的转发到下游服务
        this.transmit(needTransmit, grayDeployTransmitProerties.getHeaders(), (header) -> this.getHeader(transmitSource, header));

        // 将请求头中指定attributes的转发到下游服务
        this.transmit(needTransmit, grayDeployTransmitProerties.getAttributes(), (attribute) -> this.getAttributeString(transmitSource, attribute));

        // 将请求头中指定paramters的转发到下游服务
        this.transmit(needTransmit, grayDeployTransmitProerties.getParamters(), (paramter) -> this.getParamterString(transmitSource, paramter));

    }


    /**
     * 设置header
     */
    protected void setHeader(N needTransmit, String headerName, String headerValue) {
        if (StringUtils.isEmpty(headerValue) || StringUtils.isEmpty(headerName)) {
            return;
        }

        this.nextRequestSetHeader(needTransmit, headerName, headerValue);
    }
}
