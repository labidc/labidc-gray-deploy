package com.labidc.gray.deploy.servlet.transmit;

import com.labidc.gray.deploy.servlet.properties.GrayDeployTransmitProerties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public abstract class AbstractHeadTransmit<S, N> implements HeadTransmit<S, N> {


    private GrayDeployTransmitProerties grayDeployTransmitProerties;
    private HeadTransmitAttributeObjectTransform headTransmitAttributeObjectTransform;

    public AbstractHeadTransmit(GrayDeployTransmitProerties grayDeployTransmitProerties, HeadTransmitAttributeObjectTransform headTransmitAttributeObjectTransform) {
        this.grayDeployTransmitProerties = grayDeployTransmitProerties;
        this.headTransmitAttributeObjectTransform = headTransmitAttributeObjectTransform;
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
        return HeadTransmitAttributeUtil.attributeObjectToString(attribute, this.getAttribute(transmitSource, attribute), this.headTransmitAttributeObjectTransform);
    }

    /**
     * 设置传输的 header
     */
    @Override
    public void transmit(S transmitSource, N needTransmit) {
        // 将请求头中指定headers的转发到下游服务
        List<String> headers = grayDeployTransmitProerties.getHeaders();
        if (!CollectionUtils.isEmpty(headers)) {
            for (String header : headers) {
                if (this.nextRequestHasHeader(needTransmit, header)) {
                    continue;
                }

                this.setHeader(needTransmit, header, this.getHeader(transmitSource, header));
            }
        }

        // 将请求头中指定attributes的转发到下游服务
        List<String> attributes = grayDeployTransmitProerties.getAttributes();
        if (!CollectionUtils.isEmpty(attributes)) {
            for (String attribute : attributes) {
                if (this.nextRequestHasHeader(needTransmit, attribute)) {
                    continue;
                }

                String attributeString = this.getAttributeString(transmitSource, attribute);
                this.setHeader(needTransmit, attribute, attributeString);
            }
        }
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
