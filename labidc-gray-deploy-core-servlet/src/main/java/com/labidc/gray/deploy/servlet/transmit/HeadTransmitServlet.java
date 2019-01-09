package com.labidc.gray.deploy.servlet.transmit;

import com.labidc.gray.deploy.servlet.properties.GrayDeployTransmitProerties;
import feign.RequestTemplate;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public class HeadTransmitServlet extends AbstractHeadTransmit<HttpServletRequest, RequestTemplate> {


    public HeadTransmitServlet(GrayDeployTransmitProerties grayDeployTransmitProerties, HeadTransmitAttributeObjectTransform headTransmitAttributeObjectTransform) {
        super(grayDeployTransmitProerties, headTransmitAttributeObjectTransform);
    }

    @Override
    protected String getHeader(HttpServletRequest request, String header) {
        return request.getHeader(header);
    }

    @Override
    protected Object getAttribute(HttpServletRequest request, String attribute) {
        return request.getAttribute(attribute);
    }

    @Override
    protected boolean nextRequestHasHeader(RequestTemplate needTransmit, String header) {
        Map<String, Collection<String>> headers = needTransmit.headers();
        if(headers.isEmpty()){
            return false;
        }

        return !CollectionUtils.isEmpty(headers.get(header));
    }

    @Override
    protected void nextRequestSetHeader(RequestTemplate needTransmit, String headerName, String headerValue) {
        needTransmit.header(headerName,headerValue);
    }


}
